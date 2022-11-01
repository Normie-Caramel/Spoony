package com.mobcomp.spoony;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseHandler {

    private final static String ASSETQFILESTR = "questions.json";
    private final static String QFILESTR = "/questions.json";
    private static final int UPDATEINTERVAL = 300;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference qdb;
    private final CollectionReference qcdb;
    private Map<String, Object> qCountDoc;
    private LinkedList<Question> multiQDoc;

    private int numQuestion;
    private java.util.Date lastUpdatedTime;
    private boolean firstBoot;

    public boolean isFirstBoot() {
        return firstBoot;
    }

    public FirebaseHandler() {
        qdb = db.collection("questions");
        qcdb = db.collection("questionCount");
        qCountDoc = null;
        multiQDoc = null;
        firstBoot = true;
        getQuestionCount();
    }

    public interface DocumentCallback {
        void getDocumentSuccess(boolean success);
    }

    // get number of questions in firestore
    private void getQuestionCount() {
        qcdb.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    qCountDoc = document.getData();
                    Log.d("FSQC", String.valueOf(qCountDoc));
                    if (qCountDoc != null) {
                        numQuestion = ((Long) qCountDoc.get("amount")).intValue();
                    }
                }
            } else {
                Log.e("FSQCERR", "Error getting documents.", task.getException());
            }
        });
        lastUpdatedTime = new Date();
    }

    // increment question count in firestore
    private void incrementQuestionCount() {
        Map<String, Object> data = new HashMap<>();
        getQuestionCount();
        data.put("amount", ++numQuestion);
        qcdb.document("0").set(data).addOnSuccessListener(aVoid -> Log.d("FSQCADD", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("FSQCADDERR", "Error writing document", e));
    }

    public LinkedList<Question> getQuestions() {
        return multiQDoc;
    }

    // return a mapping of multiple questions
    // key: id, val: question string
    public void updateQuestions(String questionJSON, DocumentCallback documentCallback) {
        AtomicReference<Boolean> successFlag = new AtomicReference<>(false);
        multiQDoc = new LinkedList<>();
        if (checkUpdateTime() || questionJSON == null || firstBoot) {
            firstBoot = false;
            getQuestionCount();

            qdb.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    successFlag.set(true);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        multiQDoc.add(new Question(Integer.parseInt(document.getId()), (String) document.getData().get("question")));
                    }
                    Log.d("FSQ", String.valueOf(multiQDoc));
                    documentCallback.getDocumentSuccess(true);
                } else {
                    Log.e("FSQERR", "Error getting documents.", task.getException());
                    documentCallback.getDocumentSuccess(false);
                }
            });
        } else if (questionJSON != null) {
            Map<String, Object> data = new Gson().fromJson(questionJSON, Map.class);
            data.forEach((qid, qstr) -> multiQDoc.add(new Question(Integer.parseInt(qid), (String) qstr)));
            Log.d("FSQ", String.valueOf(multiQDoc));
            documentCallback.getDocumentSuccess(true);
        }
    }

    // return true or false if successfully added question
    protected void addNewQuestion(String question, DocumentCallback documentCallback) {
        Map<String, Object> data = new HashMap<>();
        getQuestionCount();

        // no question cleaning implemented yet (nice-to-haves)
        if (question == null || question.equals("")) {
            documentCallback.getDocumentSuccess(false);
        } else {
            data.put("question", question);
            qdb.document(String.valueOf(numQuestion)).set(data).addOnSuccessListener(aVoid -> {
                Log.d("FSQADD", "DocumentSnapshot successfully written!");
                incrementQuestionCount();
                documentCallback.getDocumentSuccess(true);
            }).addOnFailureListener(e -> {
                Log.e("FSQADDERR", "Error adding document", e);
                documentCallback.getDocumentSuccess(false);
            });
        }
    }

    private boolean checkUpdateTime() {
        int timeDiff = (int) ((new Date().getTime() - lastUpdatedTime.getTime()) / 1000);
        return timeDiff >= UPDATEINTERVAL;
    }

    protected String loadQuestionFromJSONFile(Context c, boolean firstBoot) {
        String json;
        InputStream is;
        try {
            if (firstBoot) {
                is = c.getAssets().open(ASSETQFILESTR);
                Log.d("QFILEIN1", c.getAssets().toString() + QFILESTR);
            } else {
                is = new FileInputStream(c.getFilesDir() + QFILESTR);
                Log.d("QFILEIN2", c.getFilesDir() + QFILESTR);
            }
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            Log.d("FSQ", json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    protected void saveQuestionToJSONFile(Context c, LinkedList<Question> qlist) {
        AtomicReference<Map<String, Object>> qmap = new AtomicReference<>(new HashMap<>());
        for (int i = 0; i < qlist.size(); i++) {
            qmap.get().put(String.valueOf(qlist.get(i).id), qlist.get(i).text);
        }
        try {
            FileWriter file = new FileWriter(c.getFilesDir() + QFILESTR);
            new Gson().toJson(qmap.get(), file);
            file.flush();
            file.close();
            Log.d("QFILEOUT", c.getFilesDir() + QFILESTR);
        } catch (IOException e) {
            Log.e("QFILEOUTERR", "Error in Writing: " + e.getLocalizedMessage());
        }
    }
}