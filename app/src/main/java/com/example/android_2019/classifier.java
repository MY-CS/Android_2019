package com.example.android_2019;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import androidx.appcompat.app.AlertDialog;

public class classifier extends AppCompatActivity {

    String make_file_name = "data.arff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);

        this.make_arffHeader(make_file_name);
        this.make_arffFile(make_file_name, "data_stationary.csv");
        this.make_arffFile(make_file_name, "data_walking.csv");
        this.make_arffFile(make_file_name, "data_run.csv");


        // dialogを出したい
        AlertDialog.Builder alertDialog =new AlertDialog.Builder(this);
        alertDialog.setTitle("Finish making file")
                .setMessage("finish making file")
                .setPositiveButton("OK", null)
                .show();


        // ここもエラーが出たのでcatchした
        try {
            this.build_classifier(make_file_name);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void make_arffHeader(String make_file_name) {
        try {

            FileOutputStream fileOutputstream = openFileOutput(make_file_name, MODE_APPEND);
            fileOutputstream.write("@relation data\n".getBytes());
            fileOutputstream.write("\n".getBytes());
            fileOutputstream.write("@attribute label {stationary, walking, run}\n".getBytes());
            fileOutputstream.write("@attribute acc numeric\n".getBytes());
            fileOutputstream.write("\n".getBytes());
            fileOutputstream.write("@data\n".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void make_arffFile(String write_file_name, String read_file_name) {
        String [] line = null;

        // try-with-resources
        try {
            // arffファイルを作る
            // とりあえず全部そのままStringでやってるのでそこを直さないとかも
            // arffでdoubleのデータ型はどうなるのか?

            FileOutputStream fileOutputstream = openFileOutput(write_file_name, MODE_APPEND);

            FileInputStream fileInputStream = openFileInput(read_file_name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

            //分割の処理の仕方がややこしそうなのでcsvは前の通り文字列で書き込んで
            // arffにするときにdoubleに変換して書き込むのが良さそう
            String lineBuffer;
            while ((lineBuffer = reader.readLine()) != null) {
                line = lineBuffer.split(",");
                fileOutputstream.write(line[0].getBytes());
                fileOutputstream.write(",".getBytes());
                fileOutputstream.write(line[1].getBytes());
                fileOutputstream.write("\n".getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            FileOutputStream fileOutputstream = openFileOutput(file_name, MODE_APPEND);
////            fileOutputstream.write("stationary".getBytes());
////            fileOutputstream.write(",".getBytes());
////            fileOutputstream.write(String.valueOf(data).getBytes());
////            fileOutputstream.write("\n".getBytes());
//
////             出力ファイルの作成
////             上の奴を使わないとfilesにファイルができない
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputstream));
////            FileWriter f = new FileWriter(file_name, true);
//            PrintWriter p = new PrintWriter(bw);
//
//            // 書き込み
//            p.print("stationary"); // クラスラベルのつもり
//            p.print(",");
//            p.println(data); // 合成加速度
//
//            // ファイルに書き出し閉じる
//            p.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    // これはまだテストできてない
    // まずarffをちゃんと読み込めかが分からない
    // arffでdoubleの型はどうなっているのか?
    // 全部文字列でやってるけど大丈夫なのか->ダメなら数値でファイルをどうにかして書き換える必要がある

    // エラーがめんどくさいのでスローした
    // モデルのファイルができてないのでここはできてないきがする
    public void build_classifier(String arff_file_name) throws Exception{
        // build clf
        // ファイルに今のcsvを結合して渡してみてできるか知りたい
        DataSource source = new DataSource(arff_file_name);
        Instances instances = source.getDataSet();
        System.out.println(instances);
        instances.setClassIndex(0);
        Classifier clf = new J48();
        clf.buildClassifier(instances);

        // evaluation
        // データの分割とかGridseachCVとか色々あると思うけどとりあえずサンプルどうりに
        //パラメータの決定をどうするか
        Evaluation eval = new Evaluation(instances);
        eval.evaluateModel(clf, instances);

        // ここのダイアログも出ない, やっぱりarffファイルを用いた学習ができてない
        AlertDialog.Builder result_dialog =new AlertDialog.Builder(this);
        result_dialog.setTitle("Evaluation result")
                .setMessage(eval.toSummaryString())
                .setPositiveButton("OK", null)
                .show();
//        System.out.println(eval.toSummaryString());

        // ここら辺横にエラー出てることいいのかな?
        try {
            // serialize model
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("j48.model"));
            oos.writeObject(clf);
            oos.flush();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Finish Leaningとか出したいな
        //やっぱりダイアログが表示されない, ここまで来てない
        AlertDialog.Builder alertDialog =new AlertDialog.Builder(this);
        alertDialog.setTitle("Finish learning")
                .setMessage("finish learning")
                .setPositiveButton("OK", null)
                .show();
    }
}