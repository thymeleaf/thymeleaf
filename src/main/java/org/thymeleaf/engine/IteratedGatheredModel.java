/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.engine.EventModelController.SkipBody;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;

import static org.thymeleaf.engine.ProcessorTemplateHandler.GATHERED_MODEL_CONTEXT_VARIABLE_NAME;


/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
final class IteratedGatheredModel extends AbstractGatheredModel {

    private static final String DEFAULT_STATUS_VAR_SUFFIX = "Stat";

    private enum IterationType { ZERO, ONE, MULTIPLE }


    private final IEngineContext context;
    private final TemplateMode templateMode;
    private final EventModelController eventModelController;

    private final String iterVariableName;
    private final String iterStatusVariableName;
    private final IterationStatusVar iterStatusVariable;
    private final Iterator<?> iterator;
    private final Text precedingWhitespace;

    private boolean processed;


    IteratedGatheredModel(
            final IEngineConfiguration configuration, final IEngineContext context, final EventModelController eventModelController,
            final ElementProcessorIterator suspendedProcessorIterator,
            final Model suspendedModel, final boolean suspendedModelProcessable,
            final boolean suspendedModelProcessBeforeDelegate,
            final boolean suspendedDiscardEvent, final SkipBody suspendedSkipBody, final boolean skipCloseTag,
            final String iterVariableName, final String iterStatusVariableName, final Object iteratedObject, final Text precedingWhitespace) {

        super(configuration, context, suspendedProcessorIterator, suspendedModel, suspendedModelProcessable, suspendedModelProcessBeforeDelegate, suspendedDiscardEvent, suspendedSkipBody, skipCloseTag);

        this.context = context;
        this.templateMode = context.getTemplateMode();
        this.eventModelController = eventModelController;

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

        this.processed = false;

    }


    public boolean isProcessed() {
        return this.processed;
    }



    public void process(final ITemplateHandler handler) {


        /*
         * Check this hasn't already been executed. Only one execution is allowed
         */
        if (this.processed) {
            throw new TemplateProcessingException(
                    "This delayed model has already been executed. Execution can only take place once");
        }


        /*
         * Compute the iteration type, by trying to obtain the first iterated object
         */
        final IterationType iterationType;
        boolean iterHasNext = this.iterator.hasNext();
        if (!iterHasNext) {
            iterationType = IterationType.ZERO;
        } else {
            this.iterStatusVariable.current = this.iterator.next();
            iterHasNext = this.iterator.hasNext();
            if (!iterHasNext) {
                iterationType = IterationType.ONE;
            } else {
                iterationType = IterationType.MULTIPLE;
            }
        }


        /*
         * Once the type of iteration we have has been determined, compute the models that will be used
         * for the first, the middle and the last iterations
         */
        final IterationModels iterationModel = computeIterationModels(iterationType);

        /*
         * Save the original state of the element processor iterator, so that we can set it again
         * for each iteration
         */
        final ElementProcessorIterator suspendedIterator = new ElementProcessorIterator();
        suspendedIterator.resetAsCloneOf(getSuspendedProcessorIterator());

        /*
         * Save the original state of skip variable at the event model controller
         */
        final SkipBody suspendedSkipBody = this.eventModelController.getSkipBody();
        final boolean suspendedSkipClosetag = this.eventModelController.getSkipCloseTag();


        /*
         * Perform the first iteration, if there is at least one elment (we already obtained the object)
         */
        if (iterationModel != null) {
            processIteration(iterationModel.modelFirst, handler);
        }


        /*
         * Perform iterations > 1
         */
        while (iterHasNext) {

            /*
             * Increase the iteration counter
             */
            this.iterStatusVariable.index++;

            /*
             * Obtain the new iterated objects
             */
            this.iterStatusVariable.current = this.iterator.next();

            /*
             * Recompute hasNext
             */
            iterHasNext = this.iterator.hasNext(); // precomputed in order to know when we are at the last element

            /*
             * Reset the element processor iterator to its original state (for each iteration)
             */
            getSuspendedProcessorIterator().resetAsCloneOf(suspendedIterator);

            /*
             * Reset the "skipBody" and "skipCloseTag" values at the event model controller
             */
            this.eventModelController.skip(suspendedSkipBody, suspendedSkipClosetag);


            /*
             * Select the model to be processed
             */
            final Model model = (iterHasNext? iterationModel.modelMiddle : iterationModel.modelLast);

            /*
             * Perform the iteration
             */
            processIteration(model, handler);

        }


        /*
         * DECREASE THE CONTEXT LEVEL
         * This was increased before starting gathering, when the handling of the first gathered event started.
         */
        this.context.decreaseLevel();


        /*
         * SET THE EXECUTION FLAG TO TRUE
         */
        this.processed = true;

    }


    private void processIteration(final Model model, final ITemplateHandler handler) {

        /*
         * Increase the engine context level, so that we can store the needed local variables there
         */
        this.context.increaseLevel();

        /*
         * Set the gathered model into the context
         */
        this.context.setVariable(GATHERED_MODEL_CONTEXT_VARIABLE_NAME, this);

        /*
         * Set the iteration local variables (iteration variable and iteration status variable)
         */
        this.context.setVariable(this.iterVariableName, this.iterStatusVariable.current);
        this.context.setVariable(this.iterStatusVariableName, this.iterStatusVariable);

        /*
         * PERFORM THE EXECUTION on the gathered queue, which now does not live at the current exec level, but
         * at the previous one (we protected it by increasing execution level before)
         */
        model.process(handler);

        /*
         * Decrease the engine context level, now that this iteration has been executed and we can dispose of
         * the local variables
         */
        this.context.decreaseLevel();

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
    private IterationModels computeIterationModels(final IterationType iterationType) {

        /*
         * Nothing to iterate
         */
        if (iterationType == IterationType.ZERO) {
            return null;
        }


        /*
         * Get the originally gathered model. This will serve as a base for any needed modifications
         */
        final Model innerModel = getInnerModel();
        final int gatheredModelSize = innerModel.size();


        /*
         * If there is only one iteration, we need to perform no modifications at all, whichever the template mode
         */
        if (iterationType == IterationType.ONE) {
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

        final Model modelFirst;
        final Model modelMiddle;
        final Model modelLast;

        IterationModels(final Model modelFirst, final Model modelMiddle, final Model modelLast) {
            super();
            this.modelFirst = modelFirst;
            this.modelMiddle = modelMiddle;
            this.modelLast = modelLast;
        }

    }


    
}