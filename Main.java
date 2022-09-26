package com.company;

import java.util.*;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) {

    List<Bd> ls=new ArrayList<>();
    Bd b1 = new Bd();
        b1.ref="M1";
        b1.is="true";
        b1.status="T";
        b1.acc="1";
        b1.num="A";
    Bd b2 = new Bd();
        b2.acc="2";
        b2.num="B";
    Bd b3 = new Bd();
        b3.acc="2";
        b3.num="B2";
    Bd b4 = new Bd();
        b4.acc="3";
        b4.num="D";
    ls.add(b1);
    ls.add(b2);
    ls.add(b3);
    ls.add(b4);



        Set<String> uniqueNames = new HashSet<>();

        for (Bd event : ls) {
            uniqueNames.add(event.acc);
        }

        Answer ans = new Answer();
        ans.ref=ls.get(0).ref;
        ans.is=ls.get(0).is;
        ans.status=ls.get(0).status;

List<Account> ls_acc= new ArrayList<>();
        for (String acc : uniqueNames ) {
            Account acc_obj = new Account();
            acc_obj.acc=acc;

            List<Agreem> ls_agreem=new ArrayList<>();
            for (Bd event : ls) {
                if (Objects.equals(event.acc, acc))
                {
                    Agreem agrm = new Agreem();
                    agrm.num= event.num;
                    ls_agreem.add(agrm);
                }


            }
            acc_obj.agreems=ls_agreem;
            ls_acc.add(acc_obj);

        }
        ans.accs =ls_acc;

        System.out.print(ans);

        

//        Object[] ls_f = ls.stream().map(code -> {
//       Answer ans = new Answer();
//       ans.ref=code.acc;
//            return ans;
//        }).toArray();

      //  List<String> distinctStrings = ls.stream().distinct().collect(Collectors.toList());

//        for (int i = 0; i < ls_f.length; i++) {
//            Answer ans = (Answer) ls_f[i];
//
//            System.out.println(ans.ref);
//        }
  //  System.out.print();

    }
}
