package com.mcgath.jhoveextras;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.harvard.hul.ois.jhove.Property;
import edu.harvard.hul.ois.jhove.PropertyArity;
import edu.harvard.hul.ois.jhove.PropertyType;
import edu.harvard.hul.ois.jhove.RepInfo;
import edu.harvard.hul.ois.jhove.handler.XmlHandler;

public class FilteredXmlHandler extends XmlHandler {

    private final static String NAME = "Filtered XML Handler";
    private final static String RIGHTS = "Copyright 2012 Gary McGath";
    
    private Set<String> significantPropNames;
    private final static String[] significantPropArray = {
        "foo", "bar" };
    
    public FilteredXmlHandler () {
        super ();
        significantPropNames = new HashSet<String>();
        for (String s : significantPropArray) {
            significantPropNames.add (s);
        }
    }
    
    
    /**
     * Callback allowing post-parse, pre-show analysis of object
     * representation information.
     * @param info Object representation information
     */
    @Override
    public void analyze (RepInfo info) {
        Map<String, Property> props = info.getProperty();
        //Collection<Property> propVals = props.values();
        for (String key : props.keySet()) {
            Property p = props.get(key);
            if (!containsSignificantData(p)) {
                props.remove(key);
            }
        }
    }
    
    /* Return true if this property contains either has a name
     * which is one of the significant property names, or has
     * a descendant that does. */
    private boolean containsSignificantData (Property p ) {
        if (significantPropNames.contains( p.getName())) {  
            return true;      
        }
        Object val = p.getValue();
        PropertyArity arity = p.getArity();
        PropertyType pType = p.getType();
        if (arity.equals (PropertyArity.MAP) && pType.equals (PropertyType.PROPERTY)) {
            @SuppressWarnings("rawtypes")
            Map mapVal = (Map) val;
            for (Object keyObj : mapVal.keySet()) {
                Property subVal = (Property) mapVal.get(keyObj);
                if (containsSignificantData (subVal)) {
                    return true;
                }
            }
        }
        else if (arity.equals (PropertyArity.SET) && pType.equals (PropertyType.PROPERTY)) {
            @SuppressWarnings("rawtypes")
            Set setVal = (Set) val;
            for (Object subObj : setVal) {
                if (containsSignificantData((Property) subObj)) {
                    return true;
                }
            }
        }
        else if (arity.equals (PropertyArity.LIST) && pType.equals (PropertyType.PROPERTY)) {
            @SuppressWarnings("rawtypes")
            List listVal = (List) val;
            for (Object subObj : listVal) {
                if (containsSignificantData((Property) subObj)) {
                    return true;
                }
            }
        }
        return false;
    }
}
