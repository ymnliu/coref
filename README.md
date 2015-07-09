# coref

Working copy of Coreference resolver.

Please put these projects into your workspace with all previous dependencies, e.g. ModelFileFactory. 


If you already have two directories of ident files to evaluate, you can simply use the following code:

    KeyResponseComparer krc = new KeyResponseComparer(KeyDirPath, RespondeDirPath);
    double[] score = krc.runEvalutationMUC();
    System.out.println(String.format("\t%.3f\t%.3f\t%.3f", score[0], score[1], score[2]));

The implementation of KeyResponseComparer is in project CrScore.

Please make sure your output files and key files has the similar file name
Please change the extension into yours in KeyReponseComparer.

    public final String keyFileExtension = "norm.idt";
    public final String responseFileExtension = "sgm.tmx.rdc.xml.idt";

Once the comparer find a file in keyDirPath, e.g. 9802.125.norm.idt, it will look at 9802.125.sgm.tmx.rdc.xml.idt in the responseDirPath. Comparer them and return a array of double {precision, recall, f1}.

Please double check the MatScore.MUC function before you actually use it. I only use the output of this function but the correctness of the result is not garanteed. 
