/*
  JHOVE Extras
  
  For use with the book _JHOVE Tips for Developers_ by Gary McGath
  http://www.smashwords.com
  
  This file copyright 2012 by Gary McGath

        Academic Free License
        Version 1.2

This Academic Free License applies to any original work of authorship 
(the "Original Work") whose owner (the "Licensor") has placed the 
following notice immediately following the copyright notice for the 
Original Work:

Licensed under the Academic Free License version 1.2

Grant of License. Licensor hereby grants to any person obtaining a 
copy of the Original Work ("You") a world-wide, royalty-free, 
non-exclusive, perpetual, non-sublicenseable license (1) to use, copy, 
modify, merge, publish, perform, distribute and/or sell copies of the 
Original Work and derivative works thereof, and (2) under patent claims 
owned or controlled by the Licensor that are embodied in the Original 
Work as furnished by the Licensor, to make, use, sell and offer for 
sale the Original Work and derivative works thereof, subject to the 
following conditions.

Attribution Rights. You must retain, in the Source Code of any 
Derivative Works that You create, all copyright, patent or trademark 
notices from the Source Code of the Original Work, as well as any 
notices of licensing and any descriptive text identified therein as an 
"Attribution Notice." You must cause the Source Code for any Derivative 
Works that You create to carry a prominent Attribution Notice reasonably 
calculated to inform recipients that You have modified the Original Work.

Exclusions from License Grant. Neither the names of Licensor, nor the 
names of any contributors to the Original Work, nor any of their 
trademarks or service marks, may be used to endorse or promote products 
derived from this Original Work without express prior written permission 
of the Licensor.

Warranty and Disclaimer of Warranty. Licensor warrants that the copyright 
in and to the Original Work is owned by the Licensor or that the Original 
Work is distributed by Licensor under a valid current license from the 
copyright owner. Except as expressly stated in the immediately proceeding 
sentence, the Original Work is provided under this License on an "AS IS" 
BASIS and WITHOUT WARRANTY, either express or implied, including, without 
limitation, the warranties of NON-INFRINGEMENT, MERCHANTABILITY or FITNESS 
FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY OF THE ORIGINAL 
WORK IS WITH YOU. This DISCLAIMER OF WARRANTY constitutes an essential part 
of this License. No license to Original Work is granted hereunder except 
under this disclaimer.

Limitation of Liability. Under no circumstances and under no legal theory, 
whether in tort (including negligence), contract, or otherwise, shall the 
Licensor be liable to any person for any direct, indirect, special, 
incidental, or consequential damages of any character arising as a result 
of this License or the use of the Original Work including, without 
limitation, damages for loss of goodwill, work stoppage, computer failure 
or malfunction, or any and all other commercial damages or losses. This 
limitation of liability shall not apply to liability for death or personal 
injury resulting from Licensor's negligence to the extent applicable law 
prohibits such limitation. Some jurisdictions do not allow the exclusion or 
limitation of incidental or consequential damages, so this exclusion and 
limitation may not apply to You.

License to Source Code. The term "Source Code" means the preferred form of 
the Original Work for making modifications to it and all available 
documentation describing how to modify the Original Work. Licensor hereby 
agrees to provide a machine-readable copy of the Source Code of the Original 
Work along with each copy of the Original Work that Licensor distributes. 
Licensor reserves the right to satisfy this obligation by placing a 
machine-readable copy of the Source Code in an information repository 
reasonably calculated to permit inexpensive and convenient access by You for 
as long as Licensor continues to distribute the Original Work, and by 
publishing the address of that information repository in a notice immediately 
following the copyright notice that applies to the Original Work.

Mutual Termination for Patent Action. This License shall terminate 
automatically and You may no longer exercise any of the rights granted to You 
by this License if You file a lawsuit in any court alleging that any OSI 
Certified open source software that is licensed under any license containing 
this "Mutual Termination for Patent Action" clause infringes any patent 
claims that are essential to use that software.

Right to Use. You may use the Original Work in all ways not otherwise 
restricted or conditioned by this License or by law, and Licensor promises 
not to interfere with or be responsible for such uses by You.

This license is Copyright (C) 2002 Lawrence E. Rosen. All rights reserved. 
Permission is hereby granted to copy and distribute this license without 
modification. This license may not be modified without the express written 
permission of its copyright owner.


 http://www.opensource.org/licenses/academic.php
 
*/

package com.mcgath.jhoveextras;

import java.util.Collection;
import java.util.HashMap;
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
//    private final static String[] significantPropArray = {
//        "foo", "bar" };
    
    /** Constructor */
    public FilteredXmlHandler () {
        super ();
        _name = NAME;
        _rights = RIGHTS;
        significantPropNames = new HashSet<String>();
//        for (String s : significantPropArray) {
//            significantPropNames.add (s);
//        }
    }
    
    /** Add the name of a property to be retained */
    public void addSignificantProperty (String p) {
        significantPropNames.add (p);
    }
    
    /**
     * Callback allowing post-parse, pre-show analysis of object
     * representation information. In this case, all properties
     * whose name doesn't mark them as "significant" and don't have a
     * descendant that's "significant" are pruned from the property tree.
     * 
     * @param info Object representation information
     */
    @Override
    public void analyze (RepInfo info) {
        Map<String, Property> props = info.getProperty();
        for (String key : props.keySet()) {
            Property p = props.get(key);
            if (!testForRetention(p)) {
                props.remove(key);
            }
        }
    }
    
    /* Return true if this property contains either has a name
     * which is one of the significant property names, or has
     * a descendant that does. */
    private boolean testForRetention (Property p ) {
        if (significantPropNames.contains( p.getName())) {  
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
                if (testForRetention (subVal)) {
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
                if (testForRetention((Property) subObj)) {
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
                if (testForRetention((Property) subObj)) {
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
