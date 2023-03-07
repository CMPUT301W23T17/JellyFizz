package com.example.qr_code_hunter;

public class QR_Code {

    /**
     * Returns a visual representation of the QR code
     * @param binary binary number that will determine the attributes of the visual
     * @return string representation of QR code
     */
    public String getVisualRep(String binary){
        char bin[] = binary.toCharArray();

        String rep = null;

        if(bin[0] == '1'){
            rep ="  _||||||||||||||_ ";
        }else{
            rep ="  _--------------_ ";
        }

        if(bin[1] == '1'){
            rep = rep.concat("\n { ----      ---- }");
        }else{
            rep = rep.concat("\n { ~~~>      <~~~ }");
        }

        if(bin[2] == '1'){
            rep = rep.concat("\n{| < + > || < + > |}");
        }else{
            rep = rep.concat("\n>|-[ @]--||--[ @]-|<");
        }

        if(bin[3] == '1'){
            rep = rep.concat("\n{|       ||       |}");
            rep = rep.concat("\n |      {__}      | ");
        }else{
            rep = rep.concat("\n>|       ||       |<");
            rep = rep.concat("\n |      <..>      | ");
        }

        if(bin[4] == '1'){
            rep = rep.concat("\n |   _~~~~~~~~_   | ");
        }else{
            rep = rep.concat("\n |_              _| ");
        }
        if(bin[5] == '1'){
            if(bin[6] == '1'){
                rep = rep.concat("\n  |_  (______)  _| ");
            }else{
                rep = rep.concat("\n  |_  [||||||]  _| ");
            }
            rep = rep.concat("\n   |_          _| ");
        }else{
            if(bin[6] == '1'){
                rep = rep.concat("\n |    (______)    | ");
            }else{
                rep = rep.concat("\n |    [||||||]    | ");
            }
            rep = rep.concat("\n |                | ");
        }

        if(bin[7] == '1'){
            rep = rep.concat("\n -----||||||||----- ");
        }else{
            rep = rep.concat("\n ------------------ ");
        }

        return rep;
    }
}
