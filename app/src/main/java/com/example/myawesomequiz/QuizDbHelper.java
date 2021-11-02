package com.example.myawesomequiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myawesomequiz.QuizContract.*;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static QuizDbHelper instance;
    public static synchronized QuizDbHelper getInstance(Context context)
    {
        if(instance==null)
        {
            instance = new QuizDbHelper(context.getApplicationContext());
        }
        return instance;
    }


    private static final String DATABASE_NAME = "MyAwesomeQuiz.db";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase db;
    private QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE = " CREATE TABLE " + CategoriesTable.TABLE_NAME
                + " ( " + CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoriesTable.COLUMN_QUESTION + " TEXT " + " ) ";

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NR + " INTEGER, " +
                QuestionsTable.COLUMN_DIFFICULTY + " TEXT, " +
                QuestionsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY (" + QuestionsTable.COLUMN_CATEGORY_ID +
                " ) REFERENCES " + CategoriesTable.TABLE_NAME + " ( " +
                CategoriesTable._ID + " ) " + " ON DELETE CASCADE " + " ) ";

        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillCategoriesTable();
        fillQuestionsTable();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable()
    {
        Category c1 =  new Category("Programming");
        insertCategrory(c1);
        Category c2 =  new Category("Geography");
        insertCategrory(c2);
        Category c3 = new Category("Maths");
        insertCategrory(c3);
    }

    private void fillQuestionsTable() {
        Question q1 = new Question("Programming: Easy: A is correct ?", "A", "B", "C", 1,Question.DIFFICULTY_EASY,Category.CATEGORY_PROGRAMMING);
        insertQuestion(q1);
        Question q2 = new Question("Geography: Medium: B is correct ?", "A", "B", "C", 2,Question.DIFFICULTY_MEDIUM,Category.CATEGORY_GEOGRAPHY);
        insertQuestion(q2);
        Question q3 = new Question("Math: Medium: C is correct ?", "A", "B", "C", 3,Question.DIFFICULTY_MEDIUM,Category.CATEGORY_MATH);
        insertQuestion(q3);
        Question q4 = new Question("Math: Hard: A is correct again ?", "A", "B", "C", 1,Question.DIFFICULTY_HARD,Category.CATEGORY_MATH);
        insertQuestion(q4);
        Question q5 = new Question("Non existing: Hard: B is correct again ?", "A", "B", "C", 2,Question.DIFFICULTY_HARD,10);
        insertQuestion(q5);
    }

    private void addCategory(Category category)
    {
        db = getWritableDatabase();
        insertCategrory(category);
    }

    private void addCategoryList(List<Category> categoryList)
    {
        db = getWritableDatabase();
        for(Category category:categoryList) {
            insertCategrory(category);
        }
    }

    private void insertCategrory(Category category)
    {
        ContentValues cv = new ContentValues();
        cv.put(CategoriesTable.COLUMN_QUESTION,category.getName());
        db.insert(CategoriesTable.TABLE_NAME,null,cv);
    }

    private void addQuestion(Question question)
    {
        db = getWritableDatabase();
        insertQuestion(question);
    }

    private void addQuestionList(List<Question> questionList)
    {
        db = getWritableDatabase();
        for(Question question:questionList) {
            insertQuestion(question);
        }
    }

    private void insertQuestion(Question question) {
        ContentValues cv = new ContentValues();
        cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        cv.put(QuestionsTable.COLUMN_DIFFICULTY,question.getDifficulty());
        cv.put(QuestionsTable.COLUMN_CATEGORY_ID,question.getCategoryId());
        db.insert(QuestionsTable.TABLE_NAME, null, cv);
    }

    public ArrayList<Category> getAllCategory()
    {
    ArrayList<Category> categoryList = new ArrayList<>();
    db = getReadableDatabase();

    Cursor c =db.rawQuery("SELECT * FROM " + CategoriesTable.TABLE_NAME,null);

    if(c.moveToFirst())
    {
        do {
            Category category = new Category();
                    category.setId(c.getInt(c.getColumnIndex(CategoriesTable._ID)));
                    category.setName(c.getString(c.getColumnIndex(CategoriesTable.COLUMN_QUESTION)));
                    categoryList.add(category);
        }while ((c.moveToNext()));

    }

        c.close();
    return categoryList;
    }

    public ArrayList<Question> getAllQuestion()
    {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        Cursor c = db.rawQuery(" SELECT * FROM "+ QuestionsTable.TABLE_NAME,null);

        if (c.moveToFirst())
        {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryId(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);

            }while(c.moveToNext());


        }

        return questionList;

    }

    public ArrayList<Question> getQuestion(int category,String difficulty)
    {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String selection = QuestionsTable.COLUMN_CATEGORY_ID + "= ?" + " AND " + QuestionsTable.COLUMN_DIFFICULTY + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(category),difficulty};

        Cursor c = db.query(QuestionsTable.TABLE_NAME,null,selection,selectionArgs,null,null,null);

        if (c.moveToFirst())
        {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryId(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);

            }while(c.moveToNext());


        }

        return questionList;

    }


}
