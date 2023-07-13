  String Р41 = "";
                            try {
                                Р41=getString(row, 65);
                                if (Р41!=null && Р41!="") {
                                    String[] Р41s = Р41.split("[.]");
                                    int len = Р41s.length;
                                    if (len== 2 && Р41s[1].equals("0") )
                                    {
                                        Р41 = Р41.split("[.]")[0];
                                    }
                                }
                            }
                            catch (Exception e)
                            {

                            }
                            if (Р41!=null && Р41!="")
                            {
                                r4obespech.setР41(getString(row, 65));
                            }
