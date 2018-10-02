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
package org.thymeleaf.expression;

import org.unbescape.uri.UriEscape;

/**
 * <p>
 *   Expression Object for performing URI/URL operations (esp.escaping/unescaping) inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #uris}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.4
 *
 */
public final class Uris {


    public Uris() {
        super();
    }

    

    /**
     * <p>
     *   Perform am URI path <strong>escape</strong> operation
     *   on a {@code String} input using {@code UTF-8} as encoding.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path (will not be escaped):
     * </p>
     * <ul>
     *   <li>{@code A-Z a-z 0-9}</li>
     *   <li>{@code - . _ ~}</li>
     *   <li>{@code ! $ & ' ( ) * + , ; =}</li>
     *   <li>{@code : @}</li>
     *   <li>{@code /}</li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the {@code UTF-8} and then representing each byte
     *   in {@code %HH} syntax, being {@code HH} the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be escaped.
     * @return The escaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no escaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String escapePath(final String text) {
        return UriEscape.escapeUriPath(text);
    }

    /**
     * <p>
     *   Perform am URI path <strong>unescape</strong> operation
     *   on a {@code String} input using {@code UTF-8} as encoding.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded ({@code %HH}) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use {@code UTF-8} in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be unescaped.
     * @return The unescaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no unescaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String unescapePath(final String text) {
        return UriEscape.unescapeUriPath(text);
    }

    /**
     * <p>
     *   Perform am URI path <strong>escape</strong> operation
     *   on a {@code String} input.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path (will not be escaped):
     * </p>
     * <ul>
     *   <li>{@code A-Z a-z 0-9}</li>
     *   <li>{@code - . _ ~}</li>
     *   <li>{@code ! $ & ' ( ) * + , ; =}</li>
     *   <li>{@code : @}</li>
     *   <li>{@code /}</li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in {@code %HH} syntax, being {@code HH} the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be escaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The escaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no escaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String escapePath(final String text, final String encoding) {
        return UriEscape.escapeUriPath(text, encoding);
    }

    /**
     * <p>
     *   Perform am URI path <strong>unescape</strong> operation
     *   on a {@code String} input.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded ({@code %HH}) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use the specified {@code encoding} in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be unescaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The unescaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no unescaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String unescapePath(final String text, final String encoding) {
        return UriEscape.unescapeUriPath(text, encoding);
    }



    /**
     * <p>
     *   Perform am URI path segment <strong>escape</strong> operation
     *   on a {@code String} input using {@code UTF-8} as encoding.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path segment (will not be escaped):
     * </p>
     * <ul>
     *   <li>{@code A-Z a-z 0-9}</li>
     *   <li>{@code - . _ ~}</li>
     *   <li>{@code ! $ & ' ( ) * + , ; =}</li>
     *   <li>{@code : @}</li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the {@code UTF-8} and then representing each byte
     *   in {@code %HH} syntax, being {@code HH} the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be escaped.
     * @return The escaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no escaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String escapePathSegment(final String text) {
        return UriEscape.escapeUriPathSegment(text);
    }

    /**
     * <p>
     *   Perform am URI path segment <strong>unescape</strong> operation
     *   on a {@code String} input using {@code UTF-8} as encoding.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded ({@code %HH}) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use {@code UTF-8} in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be unescaped.
     * @return The unescaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no unescaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String unescapePathSegment(final String text) {
        return UriEscape.unescapeUriPathSegment(text);
    }

    /**
     * <p>
     *   Perform am URI path segment <strong>escape</strong> operation
     *   on a {@code String} input.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path segment (will not be escaped):
     * </p>
     * <ul>
     *   <li>{@code A-Z a-z 0-9}</li>
     *   <li>{@code - . _ ~}</li>
     *   <li>{@code ! $ & ' ( ) * + , ; =}</li>
     *   <li>{@code : @}</li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in {@code %HH} syntax, being {@code HH} the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be escaped.
     * @param encoding the encoding to be used for escaping.
     * @return The escaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no escaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String escapePathSegment(final String text, final String encoding) {
        return UriEscape.escapeUriPathSegment(text, encoding);
    }

    /**
     * <p>
     *   Perform am URI path segment <strong>unescape</strong> operation
     *   on a {@code String} input.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded ({@code %HH}) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified {@code encoding} in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be unescaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The unescaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no unescaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String unescapePathSegment(final String text, final String encoding) {
        return UriEscape.unescapeUriPathSegment(text, encoding);
    }



    /**
     * <p>
     *   Perform am URI fragment identifier <strong>escape</strong> operation
     *   on a {@code String} input using {@code UTF-8} as encoding.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI fragment identifier (will not be escaped):
     * </p>
     * <ul>
     *   <li>{@code A-Z a-z 0-9}</li>
     *   <li>{@code - . _ ~}</li>
     *   <li>{@code ! $ & ' ( ) * + , ; =}</li>
     *   <li>{@code : @}</li>
     *   <li>{@code / ?}</li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the {@code UTF-8} and then representing each byte
     *   in {@code %HH} syntax, being {@code HH} the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be escaped.
     * @return The escaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no escaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String escapeFragmentId(final String text) {
        return UriEscape.escapeUriFragmentId(text);
    }

    /**
     * <p>
     *   Perform am URI fragment identifier <strong>unescape</strong> operation
     *   on a {@code String} input using {@code UTF-8} as encoding.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded ({@code %HH}) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use {@code UTF-8} in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be unescaped.
     * @return The unescaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no unescaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String unescapeFragmentId(final String text) {
        return UriEscape.unescapeUriFragmentId(text);
    }

    /**
     * <p>
     *   Perform am URI fragment identifier <strong>escape</strong> operation
     *   on a {@code String} input.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI fragment identifier (will not be escaped):
     * </p>
     * <ul>
     *   <li>{@code A-Z a-z 0-9}</li>
     *   <li>{@code - . _ ~}</li>
     *   <li>{@code ! $ & ' ( ) * + , ; =}</li>
     *   <li>{@code : @}</li>
     *   <li>{@code / ?}</li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in {@code %HH} syntax, being {@code HH} the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be escaped.
     * @param encoding the encoding to be used for escaping.
     * @return The escaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no escaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String escapeFragmentId(final String text, final String encoding) {
        return UriEscape.escapeUriFragmentId(text, encoding);
    }

    /**
     * <p>
     *   Perform am URI fragment identifier <strong>unescape</strong> operation
     *   on a {@code String} input.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded ({@code %HH}) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified {@code encoding} in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be unescaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The unescaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no unescaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String unescapeFragmentId(final String text, final String encoding) {
        return UriEscape.unescapeUriFragmentId(text, encoding);
    }



    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>escape</strong> operation
     *   on a {@code String} input using {@code UTF-8} as encoding.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI query parameter (will not be escaped):
     * </p>
     * <ul>
     *   <li>{@code A-Z a-z 0-9}</li>
     *   <li>{@code - . _ ~}</li>
     *   <li>{@code ! $ ' ( ) * , ;}</li>
     *   <li>{@code : @}</li>
     *   <li>{@code / ?}</li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the {@code UTF-8} and then representing each byte
     *   in {@code %HH} syntax, being {@code HH} the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be escaped.
     * @return The escaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no escaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String escapeQueryParam(final String text) {
        return UriEscape.escapeUriQueryParam(text);
    }

    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>unescape</strong> operation
     *   on a {@code String} input using {@code UTF-8} as encoding.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded ({@code %HH}) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use {@code UTF-8} in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be unescaped.
     * @return The unescaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no unescaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String unescapeQueryParam(final String text) {
        return UriEscape.unescapeUriQueryParam(text);
    }

    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>escape</strong> operation
     *   on a {@code String} input.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI query parameter (will not be escaped):
     * </p>
     * <ul>
     *   <li>{@code A-Z a-z 0-9}</li>
     *   <li>{@code - . _ ~}</li>
     *   <li>{@code ! $ ' ( ) * , ;}</li>
     *   <li>{@code : @}</li>
     *   <li>{@code / ?}</li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in {@code %HH} syntax, being {@code HH} the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be escaped.
     * @param encoding the encoding to be used for escaping.
     * @return The escaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no escaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String escapeQueryParam(final String text, final String encoding) {
        return UriEscape.escapeUriQueryParam(text, encoding);
    }

    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>unescape</strong> operation
     *   on a {@code String} input.
     * </p>
     * <p>
     *   This method simply calls the equivalent method in the {@code UriEscape} class from the
     *   <a href="http://www.unbescape.org">Unbescape</a> library.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded ({@code %HH}) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified {@code encoding} in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the {@code String} to be unescaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The unescaped result {@code String}. As a memory-performance improvement, will return the exact
     *         same object as the {@code text} input argument if no unescaping modifications were required (and
     *         no additional {@code String} objects will be created during processing). Will
     *         return {@code null} if {@code text} is {@code null}.
     */
    public String unescapeQueryParam(final String text, final String encoding) {
        return UriEscape.unescapeUriQueryParam(text, encoding);
    }



}
