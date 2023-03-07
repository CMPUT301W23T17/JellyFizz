package com.example.qr_code_hunter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QRtesting {

    @Test
    public void test1(){
        QR_Code mockQR = new QR_Code();
        String rep = mockQR.getVisualRep("1011000110101011001001");
        String expected = "  _||||||||||||||_ \n" +
                " { ~~~>      <~~~ }\n" +
                "{| < + > || < + > |}\n" +
                "{|       ||       |}\n" +
                " |      {__}      | \n" +
                " |_              _| \n" +
                " |    [||||||]    | \n" +
                " |                | \n" +
                " -----||||||||----- ";
        assertEquals(expected, rep);
    }

}
