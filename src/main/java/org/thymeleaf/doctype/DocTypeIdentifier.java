package org.thymeleaf.doctype;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.regex.Pattern;

import org.thymeleaf.util.Validate;

/**
 * <p>
 *   This class represents each of the components of a complete
 *   DOCTYPE declaration: <tt>PUBLICID</tt> or <tt>SYSTEMID</tt>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 * @since 1.0
 *
 */
public final class DocTypeIdentifier implements Serializable {

    private static final long serialVersionUID = -2899688477489960285L;

    
    public static final DocTypeIdentifier ANY = new DocTypeIdentifier(false, true, null);
    public static final DocTypeIdentifier NONE = new DocTypeIdentifier(true, false, null);
    
    private final boolean none;
    private final boolean any;
    private final String value;
    private final Pattern valuePattern;
    

    public static DocTypeIdentifier forAny() {
        return ANY;
    }

    public static DocTypeIdentifier forNone() {
        return NONE;
    }
    
    public static DocTypeIdentifier forValue(final String value) {
        if (value == null) {
            return NONE;
        }
        return new DocTypeIdentifier(false, false, value);
    }
    
    
    private DocTypeIdentifier(final boolean none, final boolean any, final String value) {
        super();
        this.none = none;
        this.any = any;
        this.value = value;
        this.valuePattern = null == value ? null : Pattern.compile(value);
    }

    public boolean isNone() {
        return this.none;
    }

    public boolean isAny() {
        return this.any;
    }

    public String getValue() {
        return this.value;
    }
    
    
    public boolean matches(final String identifier) {
        boolean matches = false;
        if (isAny()) {
            matches = true;
        } else {
            if (isNone()) {
                matches = (identifier == null);
            } else {
                if (identifier != null) {
                    matches = this.valuePattern.matcher(identifier).matches();
                }
            }
        }
        return matches;
    }
    
    
    public boolean matches(final DocTypeIdentifier identifier) {
        Validate.notNull(identifier, "Identifier cannot be null");
        boolean matches = false;
        if (isAny()) {
            matches = true;
        } else {
            if (isNone()) {
                matches = identifier.isNone();
            } else {
                matches = this.value.equals(identifier.value);
            }
        }
        return matches;
    }
    
    
    @Override
    public String toString() {
        if (isNone()) {
            return "(NONE)";
        }
        if (isAny()) {
            return "(ANY)";
        }
        return this.value;
    }

    

    
    public void write(final Writer writer) throws IOException {
        if (!isNone()) {
            if (isAny()) {
                // An "any" doctypeid should never be written, but anyway... 
                writer.write("*");
            } else {
                writer.write(this.value);
            }
        }
    }

    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.any ? 1231 : 1237);
        result = prime * result + (this.none ? 1231 : 1237);
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }

    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DocTypeIdentifier other = (DocTypeIdentifier) obj;
        if (this.any != other.any) {
            return false;
        }
        if (this.none != other.none) {
            return false;
        }
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
    
    
}
