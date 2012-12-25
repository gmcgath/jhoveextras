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

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.junit.Test;

import edu.harvard.hul.ois.jhove.OutputHandler;
import edu.harvard.hul.ois.jhove.module.PdfModule;

public class FilteredXmlHandlerTest {

    @Test
    public void test() {
        ExampleFrame tf = new ExampleFrame (System.getProperty ("user.home") +
                "/jhove/conf/jhove.conf");
        FilteredXmlHandler handler = new FilteredXmlHandler();
        handler.addSignificantProperty("Producer");
        handler.addSignificantProperty("CreationDate");
        //handler.addSignificantProperty("PDFMetadata");   // ** SANITY CHECK 
        
        StringWriter wrtr = new StringWriter ();
        PrintWriter pwrtr = new PrintWriter (wrtr);
        handler.setWriter(pwrtr);
        
        try {
            PdfModule module = new PdfModule ();
            module.init("");
            module.setDefaultParams(new ArrayList<String>());
            tf.processFile("testfiles/test1.pdf", module, handler);
            String output = wrtr.getBuffer().toString();
            assertTrue (output.indexOf ("<name>Producer") > 0);
            assertTrue (output.indexOf ("<name>CreationDate") > 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail (e.getClass().getName());
        }
    }

}
