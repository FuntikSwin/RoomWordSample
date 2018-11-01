package com.home.fomakin.roomwordsample.domain;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.home.fomakin.roomwordsample.domain.dao.WordDao;
import com.home.fomakin.roomwordsample.domain.entity.Word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Database(entities = {Word.class}, version = 1)
public abstract class WordRoomDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "test.db";
    public abstract WordDao wordDao();

    private static volatile WordRoomDatabase INSTANCE;

    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static void copyDatabase(Context context) {
        File dbPath = context.getDatabasePath(DATABASE_NAME);
        if (!dbPath.exists()) {
            try (InputStream is = context.getAssets().open("databases/" + DATABASE_NAME)) {
                try (OutputStream os = new FileOutputStream(dbPath)) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = is.read(buffer, 0, 8192)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static WordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordRoomDatabase.class) {
                if (INSTANCE == null) {
                    copyDatabase(context);
                    // Create database
                    INSTANCE = Room
                            //.databaseBuilder(context.getApplicationContext(), WordRoomDatabase.class, "word_database")
                            .databaseBuilder(context.getApplicationContext(), WordRoomDatabase.class, DATABASE_NAME)
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final WordDao wordDao;

        PopulateDbAsync(WordRoomDatabase db) {
            wordDao = db.wordDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            /*wordDao.deleteAll();
            Word word = new Word("Hello");
            wordDao.insert(word);
            word = new Word("World");
            wordDao.insert(word);*/
            return null;
        }
    }

}
