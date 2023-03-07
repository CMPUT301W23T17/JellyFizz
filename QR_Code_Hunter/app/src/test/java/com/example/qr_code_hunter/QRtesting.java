package com.example.qr_code_hunter;

import org.junit.Test;

public class QRtesting {

    @Test
    public void test1(){
        QR_Code mockQR = new QR_Code();
        String rep = mockQR.getVisualRep("11010010");
        System.out.println(rep);
    }

}
