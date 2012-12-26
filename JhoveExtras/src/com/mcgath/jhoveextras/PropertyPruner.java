package com.mcgath.jhoveextras;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.harvard.hul.ois.jhove.Property;
import edu.harvard.hul.ois.jhove.PropertyArity;
import edu.harvard.hul.ois.jhove.PropertyType;
import edu.harvard.hul.ois.jhove.RepInfo;

/** This class implements pruning of the Properties in a RepInfo object so
 *  that only properties with selected names are retained. If a Property
 *  has a "signficant" name, it is retained along with all its descendants.
 */
public class PropertyPruner {

    private RepInfo repInfo;
    
    public PropertyPruner (RepInfo info) {
        repInfo = info;
    }
    
    public void prune(Set<String> propNames) {
        Map<String, Property> props = repInfo.getProperty();
        for (String key : props.keySet()) {
            Property p = props.get(key);
            if (!testForRetention(p, propNames)) {
                props.remove(key);
            }
        }
    }

    /* Return true if this property contains either has a name
     * which is one of the significant property names, or has
     * a descendant that does. */
    private boolean testForRetention (Property p, Set<String> propNames) {
        if (propNames.contains( p.getName())) {  
            return true;      
        }
        Object val = p.getValue();
        PropertyArity arity = p.getArity();
        PropertyType pType = p.getType();
        if (arity.equals (PropertyArity.MAP) && pType.equals (PropertyType.PROPERTY)) {
            @SuppressWarnings("rawtypes")
            Map mapVal = (Map) val;
            boolean retain = false;
            // I'm not sure it's necessary to do it this way, but it seems safer,
            // since we're removing stuff inside the loop.
            Object[] keys = mapVal.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                Object keyObj = keys[i];
                Property subVal = (Property) mapVal.get(keyObj);
                if (testForRetention (subVal, propNames)) {
                    retain = true;
                }
                else {
                    mapVal.remove(keyObj);
                }
            }
            return retain;
        }
        else if (arity.equals (PropertyArity.SET) && pType.equals (PropertyType.PROPERTY)) {
            @SuppressWarnings("rawtypes")
            Set setVal = (Set) val;
            boolean retain = false;
            // Same thing here -- can we safely iterate through a set
            // while removing things from it?
            Object[] objects = setVal.toArray();
            for (int i = 0; i < objects.length; i++) {
                Object subObj = objects[i];
                if (testForRetention((Property) subObj, propNames)) {
                    retain = true;
                }
                else {
                    setVal.remove(subObj);
                }
            }
            return retain;
        }
        else if (arity.equals (PropertyArity.LIST) && pType.equals (PropertyType.PROPERTY)) {
            @SuppressWarnings("rawtypes")
            List listVal = (List) val;
            boolean retain = false;
            Object[] objects = listVal.toArray();
            for (int i = 0; i < objects.length; i++) {
                Object subObj = objects[i];
                if (testForRetention((Property) subObj, propNames)) {
                    retain = true;
                }
                else {
                    listVal.remove (subObj); // this isn't safe, is it?
                }
            }
            return retain;
        }
        return false;
    }

}
