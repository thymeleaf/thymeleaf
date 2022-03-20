/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.thymeleaf.engine;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.engine.TemplateModelController.SkipBody;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;


/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
final class IteratedGatheringModelProcessable extends AbstractGatheringModelProcessable {

    private static final String DEFAULT_STATUS_VAR_SUFFIX = "Stat";

    enum IterationWhiteSpaceHandling {ZERO_ITER, SINGLE_ITER, MULTIPLE_ITER}


    private final IEngineContext context;
    private final TemplateMode templateMode;

    private final String iterVariableName;
    private final String iterStatusVariableName;
    private final IterationStatusVar iterStatusVariable;
    private final Iterator<?> iterator;
    private final Text precedingWhitespace;

    private IterationModels iterationModels;

    private DataDrivenTemplateIterator dataDrivenIterator;

    private int iter;
    private int iterOffset;
    private Model iterModel;



    IteratedGatheringModelProcessable(
            final IEngineConfiguration configuration, ProcessorTemplateHandler processorTemplateHandler, final IEngineContext context,
            final TemplateModelController modelController, final TemplateFlowController flowController,
            final SkipBody gatheredSkipBody, final boolean gatheredSkipCloseTag,
            final ProcessorExecutionVars processorExecutionVars,
            final String iterVariableName, final String iterStatusVariableName, final Object iteratedObject, final Text precedingWhitespace) {

        super(configuration, processorTemplateHandler, context, modelController, flowController, gatheredSkipBody, gatheredSkipCloseTag, processorExecutionVars);

        this.context = context;
        this.templateMode = context.getTemplateMode();

        this.iterator = computeIteratedObjectIterator(iteratedObject);

        this.iterVariableName = iterVariableName;

        if (StringUtils.isEmptyOrWhitespace(iterStatusVariableName)) {
            // If no name has been specified for the status variable, we will use the same as the iter var + "Stat"
            this.iterStatusVariableName = iterVariableName + DEFAULT_STATUS_VAR_SUFFIX;
        } else {
            this.iterStatusVariableName = iterStatusVariableName;
        }

        this.iterStatusVariable = new IterationStatusVar();
        this.iterStatusVariable.index = 0;
        this.iterStatusVariable.size = computeIteratedObjectSize(iteratedObject);

        this.precedingWhitespace = precedingWhitespace;

        if (this.iterator != null && this.iterator instanceof DataDrivenTemplateIterator) {
            this.dataDrivenIterator = (DataDrivenTemplateIterator) iterator;
        } else {
            this.dataDrivenIterator = null;
        }

        this.iter = 0;
        this.iterOffset = 0;
        this.iterModel = null;

    }




    @Override
    public ProcessorExecutionVars initializeProcessorExecutionVars() {
        // This will be called once per iteration, so we need to clone it every time it is requested
        return super.initializeProcessorExecutionVars().cloneVars();
    }



    public boolean process() {

        final TemplateFlowController flowController = getFlowController();

        /*
         * First, check the stopProcess flag to make sure we can actually do something
         */
        if (flowController != null && flowController.stopProcessing) {
            return false;
        }

        /*
         * Check if the data driven iterator (if there is one) is signaling us that it is paused (i.e. that it
         * currently has no data, but might have in the future because 'complete'  has not been signaled yet).
         * NOTE this check only makes sense if there is no iteration currently ongoing (stopped in a previous execution)
         */
        if (this.iterModel == null) {
            if (flowController != null && this.dataDrivenIterator != null && this.dataDrivenIterator.isPaused()) {
                flowController.stopProcessing = true;
                return false;
            }
        }


        /*
         * We will need to compute the iteration models before the actual first iteration starts. These
         * models will give us instructions on how whitespace should be dealt with during iteration.
         */
        if (this.iterationModels == null) {

            final IterationWhiteSpaceHandling iterationWhiteSpaceHandling;

            if (this.dataDrivenIterator != null) {

                // If iteration is data-driven, we will be possibly executing this iteration model many times,
                // and for each time the data-driven iterator will contain a different set of data which will be
                // exhausted (and then this model might be called again). So it is very possible that at a specific
                // iteration we might not now if there are more than one value to be iterated, maybe those values
                // haven't even been generated by the data source (and we should not just cache values waiting for
                // the next one to come, because pushing values to the interface as they are generated might be
                // important in these cases).
                //
                // Because of this, we will only consider white-spacing for ZERO items (if we reached this point
                // and we still have zero items, it's because 'complete' has been signaled), or for ONE item when
                // we have one or many (so no white space magic will be performed)
                if (this.iterator.hasNext()) {
                    iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.SINGLE_ITER;
                    this.iterStatusVariable.current = this.iterator.next();
                } else {
                    iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.ZERO_ITER;
                }

            } else {

                // This is an unthrottled iterator, so we will use hasNext() more freely to know
                // about up to two positions in the iterator

                if (this.iterator.hasNext()) {
                    this.iterStatusVariable.current = this.iterator.next();
                    if (this.iterator.hasNext()) {
                        iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.MULTIPLE_ITER;
                    } else {
                        iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.SINGLE_ITER;
                    }
                } else {
                    iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.ZERO_ITER;
                }

            }


            /*
             * Once the type of iteration we have has been determined, compute the models that will be used
             * for the first, the middle and the last iterations
             */
            this.iterationModels = computeIterationModels(iterationWhiteSpaceHandling);

        }


        /*
         * Compute the first iteration. Note this is a separate block from the computation of the iteration
         * models because we can enter here more than once if the execution of the first iteration is stopped
         * unfinished (e.g. by lack of output buffer)
         */
        if (this.iter == 0) {

            /*
             * Do perform the first iteration, if there is at least one element (we already obtained the object)
             */
            if (!this.iterationModels.isEmpty()) { // Will only be empty if we have zero iterations

                boolean iterationIsNew = false;

                if (this.iterModel == null) {
                    this.iterModel = this.iterationModels.modelFirst;
                    iterationIsNew = true;
                }

                if (!processIterationModel(flowController, iterationIsNew)) {
                    // We were NOT able to finish processing the iteration model - something stopped us (buffer size),
                    // so we just need to return false and wait for this to be executed again.
                    // Note that we do NOT set the iterModel back to null because we will need to finish processing it later
                    return false;
                }

                this.iter++;
                this.iterOffset = 0;
                this.iterModel = null;

                /*
                 * Check if, after this iteration, the data driven iterator (if there is one) is signaling us that it is
                 * paused (i.e. that it currently has no more data, but might have in the future because 'complete'  has
                 * not been signaled yet).
                 */
                if (flowController != null && this.dataDrivenIterator != null && this.dataDrivenIterator.isPaused()) {
                    flowController.stopProcessing = true;
                    return false;
                }

            } else {

                // There were no iterations, but we need to reset the skip values anyway. However,
                // given there will be no iteration being processed (which is the process in charge of resetting
                // the skip flags after each iteration), we will need to manually check whether the skip
                // flags were previously set to all-but-first, and in such case avoid the next element from
                // being processed by setting skip to skip_elements.
                resetGatheredSkipFlagsAfterNoIterations();

            }

        }


        /*
         * Perform iterations > 1
         */
        while (this.iterModel != null || this.iterator.hasNext()) {

            boolean iterationIsNew = false;

            /*
             * Initialize the iteration, if we are at the beginning of it
             */
            if (this.iterModel == null && this.iterOffset == 0) {

                /*
                 * Increase the iteration counter
                 */
                this.iterStatusVariable.index++;

                /*
                 * Obtain the new iterated objects
                 */
                this.iterStatusVariable.current = this.iterator.next();

                iterationIsNew = true;

            }

            /*
             * Select the model to be processed
             */
            if (this.iterModel == null) {
                this.iterModel =
                        (this.iterator.hasNext() ? this.iterationModels.modelMiddle : this.iterationModels.modelLast);
            }

            /*
             * Perform the iteration
             */
            if (!processIterationModel(flowController, iterationIsNew)) {
                // We were NOT able to finish processing the iteration model - something stopped us (buffer size),
                // so we just need to return false and wait for this to be executed again.
                // Note that we do NOT set the iterModel back to null because we will need to finish processing it later
                return false;
            }

            this.iter++;
            this.iterOffset = 0;
            this.iterModel = null;

            /*
             * Check if, after this iteration, the data driven iterator (if there is one) is signaling us that it is
             * paused (i.e. that it currently has no more data, but might have in the future because 'complete'  has
             * not been signaled yet).
             */
            if (flowController != null && this.dataDrivenIterator != null && this.dataDrivenIterator.isPaused()) {
                flowController.stopProcessing = true;
                return false;
            }

        }


        /*
         * DECREASE THE CONTEXT LEVEL
         * This was increased before starting gathering, when the handling of the first gathered event started.
         */
        this.context.decreaseLevel();


        /*
         * RETURN true FOR 'processed'
         */
        return true;

    }



    private boolean processIterationModel(final TemplateFlowController flowController, final boolean iterationIsNew) {

        if (iterationIsNew) {

            /*
             * Increase the engine context level, so that we can store the needed local variables there
             */
            this.context.increaseLevel();

            /*
             * Set the iteration local variables (iteration variable and iteration status variable)
             */
            this.context.setVariable(this.iterVariableName, this.iterStatusVariable.current);
            this.context.setVariable(this.iterStatusVariableName, this.iterStatusVariable);

            /*
             * Reset the "skipBody" and "skipCloseTag" values at the event model controller, and also set this
             * synthetic model into the processor handler so that it can be used by the executed events
             */
            prepareProcessing();

            /*
             * Signal the data driven iterator that the execution of this particular iteration will start
             */
            if (this.dataDrivenIterator != null) {
                this.dataDrivenIterator.startIteration();
            }

        }

        /*
         * PERFORM THE EXECUTION on the gathered queue, which now does not live at the current exec level, but
         * at the previous one (we protected it by increasing execution level before)
         */
        this.iterOffset += this.iterModel.process(getProcessorTemplateHandler(), this.iterOffset, flowController);

        /*
         * Check if we have completed the iteration, returning false if not
         */
        if (flowController != null && (this.iterOffset < this.iterModel.queueSize || flowController.stopProcessing)) {
            return false;
        }

        /*
         * Decrease the engine context level, now that this iteration has been executed and we can dispose of
         * the local variables
         */
        this.context.decreaseLevel();

        /*
         * Signal the data driven iterator that the execution of this particular iteration has finished completely
         * (though we still might have some contents overflown in the output buffers)
         */
        if (this.dataDrivenIterator != null) {
            this.dataDrivenIterator.finishIteration();
        }

        /*
         * If we reached this point, it's because we were able to complete the iteration
         */
        return true;

    }








    /*
     * Whenever possible, compute the total size of the iterated object. Note sometimes we will not be able
     * to compute this size without traversing the entire collection/iterator (which we want to avoid), so
     * null will be returned.
     */
    private static Integer computeIteratedObjectSize(final Object iteratedObject) {
        if (iteratedObject == null) {
            return Integer.valueOf(0);
        }
        if (iteratedObject instanceof Collection<?>) {
            return Integer.valueOf(((Collection<?>) iteratedObject).size());
        }
        if (iteratedObject instanceof Map<?,?>) {
            return Integer.valueOf(((Map<?, ?>) iteratedObject).size());
        }
        if (iteratedObject.getClass().isArray()) {
            return Integer.valueOf(Array.getLength(iteratedObject));
        }
        if (iteratedObject instanceof Iterable<?>) {
            return null; // Cannot determine before actually iterating
        }
        if (iteratedObject instanceof Iterator<?>) {
            return null; // Cannot determine before actually iterating
        }
        return Integer.valueOf(1); // In this case, we will iterate the object as a collection of size 1
    }




    /*
     * Creates, from the iterated object (e.g. right part of a th:each expression), the iterator that will be used.
     */
    private static Iterator<?> computeIteratedObjectIterator(final Object iteratedObject) {
        if (iteratedObject == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        if (iteratedObject instanceof Collection<?>) {
            return ((Collection<?>)iteratedObject).iterator();
        }
        if (iteratedObject instanceof Map<?,?>) {
            return ((Map<?,?>)iteratedObject).entrySet().iterator();
        }
        if (iteratedObject.getClass().isArray()) {
            return new Iterator<Object>() {

                protected final Object array = iteratedObject;
                protected final int length = Array.getLength(this.array);
                private int i = 0;

                public boolean hasNext() {
                    return this.i < this.length;
                }

                public Object next() {
                    return Array.get(this.array, i++);
                }

                public void remove() {
                    throw new UnsupportedOperationException("Cannot remove from an array iterator");
                }

            };
        }
        if (iteratedObject instanceof Iterable<?>) {
            return ((Iterable<?>)iteratedObject).iterator();
        }
        if (iteratedObject instanceof Iterator<?>) {
            return (Iterator<?>)iteratedObject;
        }
        if (iteratedObject instanceof Enumeration<?>) {
            return new Iterator<Object>() {

                protected final Enumeration<?> enumeration = (Enumeration<?>)iteratedObject;


                public boolean hasNext() {
                    return this.enumeration.hasMoreElements();
                }

                public Object next() {
                    return this.enumeration.nextElement();
                }

                public void remove() {
                    throw new UnsupportedOperationException("Cannot remove from an Enumeration iterator");
                }

            };
        }
        return Collections.singletonList(iteratedObject).iterator();
    }







    /*
     * Internal-only method, meant to reshape the gathered model so that white space is adequately handled
     * during iteration. As a result, this method will produce different Model object for the first, the middle
     * and the last iterations.
     */
    private IterationModels computeIterationModels(final IterationWhiteSpaceHandling iterationWhiteSpaceHandling) {

        /*
         * Nothing to iterate
         */
        if (iterationWhiteSpaceHandling == IterationWhiteSpaceHandling.ZERO_ITER) {
            return IterationModels.EMPTY;
        }


        /*
         * Get the originally gathered model. This will serve as a base for any needed modifications
         */
        final Model innerModel = getInnerModel();
        final int gatheredModelSize = innerModel.size();


        /*
         * If there is only one iteration, we need to perform no modifications at all, whichever the template mode
         */
        if (iterationWhiteSpaceHandling == IterationWhiteSpaceHandling.SINGLE_ITER) {
            return new IterationModels(innerModel, innerModel, innerModel);
        }


        /*
         * If template mode is a markup one, we will only need to take care of the existence of a preceding white space
         */
        if (!this.templateMode.isText()) {
            if (this.precedingWhitespace != null) {
                final Model modelWithWhiteSpace = new Model(innerModel);
                modelWithWhiteSpace.insert(0, this.precedingWhitespace);
                return new IterationModels(innerModel, modelWithWhiteSpace, modelWithWhiteSpace);
            }
            return new IterationModels(innerModel, innerModel, innerModel);
        }


        /*
         * We are in a textual template mode, and it might be possible to fiddle a bit with whitespaces at the beginning
         * and end of the body, so that iterations look better.
         *
         * The goal is that this:
         * ---------------------
         * List:
         * [# th:each="i : ${items}"]
         *   - [[${i}]]
         * [/]
         * ---------------------
         * ...doesn't look like:
         * ---------------------
         * List:
         *
         *   - [[${i}]]
         *
         *   - [[${i}]]
         *
         *   - [[${i}]]
         * ---------------------
         * ...but instead like:
         * ---------------------
         * List:
         *
         *   - [[${i}]]
         *   - [[${i}]]
         *   - [[${i}]]
         * ---------------------
         * And in order to do this, the steps to be taken will be:
         *
         *   - Check that the iterated block starts with an 'open element' and ends with a 'close element'. If not,
         *     don't apply any of this.
         *   - Except for the first iteration, remove all whitespace after the 'open element', until the
         *     first '\n' (and remove that too).
         *   - Except for the last iteration, remove all whitespace after the last '\n' (not including it) and before
         *     the 'close element'.
         */

        if (innerModel.size() <= 2) {
            // This does only contain the template open + close events -- nothing to be done
            return new IterationModels(innerModel, innerModel, innerModel);
        }

        int firstBodyEventCutPoint = -1;
        int lastBodyEventCutPoint = -1;

        final ITemplateEvent firstBodyEvent = innerModel.get(1); // we know there is at least one body event
        Text firstTextBodyEvent = null;
        if (innerModel.get(0) instanceof OpenElementTag && firstBodyEvent instanceof IText) {

            firstTextBodyEvent = Text.asEngineText((IText)firstBodyEvent);

            final int firstTextEventLen = firstTextBodyEvent.length();
            int i = 0;
            char c;
            while (i < firstTextEventLen && firstBodyEventCutPoint < 0) {
                c = firstTextBodyEvent.charAt(i);
                if (c == '\n') {
                    firstBodyEventCutPoint = i + 1;
                    break; // we've already assigned the value we were looking for
                } else if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                } else {
                    // We will not be able to perform any whitespace reduction here
                    break;
                }
            }

        }

        final ITemplateEvent lastBodyEvent = innerModel.get(gatheredModelSize - 2);
        Text lastTextBodyEvent = null;
        if (firstBodyEventCutPoint >= 0 &&
                innerModel.get(gatheredModelSize - 1) instanceof CloseElementTag && lastBodyEvent instanceof IText) {

            lastTextBodyEvent = Text.asEngineText((IText)lastBodyEvent);

            final int lastTextEventLen = lastTextBodyEvent.length();
            int i = lastTextEventLen - 1;
            char c;
            while (i >= 0 && lastBodyEventCutPoint < 0) {
                c = lastTextBodyEvent.charAt(i);
                if (c == '\n') {
                    lastBodyEventCutPoint = i + 1;
                    break; // we've already assigned the value we were looking for
                } else if (Character.isWhitespace(c)) {
                    i--;
                    continue;
                } else {
                    // We will not be able to perform any whitespace reduction here
                    break;
                }
            }

        }


        /*
         * If there is no reason to perform any modifications, just use the gathered model
         */
        if (firstBodyEventCutPoint < 0 || lastBodyEventCutPoint < 0) {
            // We don't have the scenario required for performing the needed whitespace collapsing operation
            return new IterationModels(innerModel, innerModel, innerModel);
        }


        /*
         * At this point, we are sure that we will want to perform modifications on the first/last whitespaces
         */

        if (firstBodyEvent == lastBodyEvent) {

            // If the first and the last event are actually the same, we need to take better care of how we manage whitespace
            final Text textForFirst = new Text(firstTextBodyEvent.subSequence(0, lastBodyEventCutPoint));
            final Text textForMiddle = new Text(firstTextBodyEvent.subSequence(firstBodyEventCutPoint, lastBodyEventCutPoint));
            final Text textForLast = new Text(firstTextBodyEvent.subSequence(firstBodyEventCutPoint, firstTextBodyEvent.length()));

            final Model modelFirst = new Model(innerModel);
            modelFirst.replace(1, textForFirst);

            final Model modelMiddle = new Model(innerModel);
            modelMiddle.replace(1, textForMiddle);

            final Model modelLast = new Model(innerModel);
            modelLast.replace(1, textForLast);

            return new IterationModels(modelFirst, modelMiddle, modelLast);

        }

        // At this point, we know the first and last body events are different objects

        final Model modelFirst = new Model(innerModel);
        final Model modelMiddle = new Model(innerModel);
        final Model modelLast = new Model(innerModel);

        if (firstBodyEventCutPoint > 0) {
            final Text headTextForMiddleAndMax = new Text(firstTextBodyEvent.subSequence(firstBodyEventCutPoint, firstTextBodyEvent.length()));
            modelMiddle.replace(1, headTextForMiddleAndMax);
            modelLast.replace(1, headTextForMiddleAndMax);
        }

        if (lastBodyEventCutPoint < lastTextBodyEvent.length()) {
            final Text tailTextForFirstAndMiddle = new Text(lastTextBodyEvent.subSequence(0, lastBodyEventCutPoint));
            modelFirst.replace(gatheredModelSize - 2, tailTextForFirstAndMiddle);
            modelMiddle.replace(gatheredModelSize - 2, tailTextForFirstAndMiddle);
        }

        return new IterationModels(modelFirst, modelMiddle, modelLast);

    }





    private static final class IterationModels  {

        static IterationModels EMPTY = new IterationModels(null, null, null);

        final Model modelFirst;
        final Model modelMiddle;
        final Model modelLast;
        final boolean empty;

        IterationModels(final Model modelFirst, final Model modelMiddle, final Model modelLast) {
            super();
            this.modelFirst = modelFirst;
            this.modelMiddle = modelMiddle;
            this.modelLast = modelLast;
            this.empty = (this.modelFirst == null && this.modelMiddle == null && this.modelLast == null);
        }

        boolean isEmpty() {
            return this.empty;
        }

    }


    
}