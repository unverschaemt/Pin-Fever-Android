package net.unverschaemt.pinfever;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumnsFriends = {MySQLiteHelper.FRIENDS_COLUMN_ID,
            MySQLiteHelper.FRIENDS_COLUMN_USERNAME, MySQLiteHelper.FRIENDS_COLUMN_SCORE};
    private String[] allColumnsGames = {MySQLiteHelper.GAMES_COLUMN_ID,
            MySQLiteHelper.GAMES_COLUMN_STATE, MySQLiteHelper.GAMES_COLUMN_ACTIVE_ROUND};
    private String[] allColumnsQuestions = {MySQLiteHelper.QUESTIONS_COLUMN_ID,
            MySQLiteHelper.QUESTIONS_COLUMN_TEXT, MySQLiteHelper.QUESTIONS_COLUMN_ROUND,
            MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LAT, MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LONG,
            MySQLiteHelper.QUESTIONS_COLUMN_STATE, MySQLiteHelper.QUESTIONS_COLUMN_PARTICIPANT_WHO_ONE};
    private String[] allColumnsRounds = {MySQLiteHelper.ROUNDS_COLUMN_ID,
            MySQLiteHelper.ROUNDS_COLUMN_CATEGORY, MySQLiteHelper.ROUNDS_COLUMN_GAME};
    private String[] allColumnsTurninformation = {MySQLiteHelper.TURNINFORMATION_COLUMN_ID,
            MySQLiteHelper.TURNINFORMATION_COLUMN_QUESTION, MySQLiteHelper.TURNINFORMATION_COLUMN_PARTICIPANT,
            MySQLiteHelper.TURNINFORMATION_COLUMN_LAT, MySQLiteHelper.TURNINFORMATION_COLUMN_LONG,
            MySQLiteHelper.TURNINFORMATION_COLUMN_DISTANCE};
    private String[] allColumnsParticipants = {MySQLiteHelper.PARTICIPANTS_COLUMN_ID,
            MySQLiteHelper.PARTICIPANTS_COLUMN_PLAYER, MySQLiteHelper.PARTICIPANTS_COLUMN_GAME,
            MySQLiteHelper.PARTICIPANTS_COLUMN_STATE, MySQLiteHelper.PARTICIPANTS_COLUMN_SCORE};

    public DataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public User createFriend(User friend) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.FRIENDS_COLUMN_ID, friend.getId());
        values.put(MySQLiteHelper.FRIENDS_COLUMN_USERNAME, friend.getUserName());
        values.put(MySQLiteHelper.FRIENDS_COLUMN_SCORE, friend.getScore());
        database.insert(MySQLiteHelper.TABLE_FRIENDS, null, values);
        return friend;
    }

    public void deleteFriend(User user) {
        String id = user.getId();
        System.out.println("Friend deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_FRIENDS, MySQLiteHelper.FRIENDS_COLUMN_ID
                + " = \"" + id + "\"", null);
    }

    public List<User> getAllFriends() {
        List<User> friends = new ArrayList<User>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumnsFriends, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User friend = cursorToFriend(cursor);
            friends.add(friend);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return friends;
    }

    private void updateFriend(User friend) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.FRIENDS_COLUMN_ID, friend.getId());
        values.put(MySQLiteHelper.FRIENDS_COLUMN_USERNAME, friend.getUserName());
        values.put(MySQLiteHelper.FRIENDS_COLUMN_SCORE, friend.getScore());
        database.update(MySQLiteHelper.TABLE_FRIENDS, values, MySQLiteHelper.FRIENDS_COLUMN_ID + " = \"" + friend.getId() + "\"", null);
    }

    public void updateFriends(List<User> newFriends) {
        List<User> oldFriends = getAllFriends();
        for (User friend : newFriends) {
            if (oldFriends.contains(friend)) {
                updateFriend(friend);
            } else {
                createFriend(friend);
            }
        }
        oldFriends.removeAll(newFriends);
        for (User friend : oldFriends) {
            deleteFriend(friend);
        }
    }

    public User getFriendForId(String id) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_FRIENDS + " WHERE "
                + MySQLiteHelper.FRIENDS_COLUMN_ID + " = \"" + id + "\"";

        Cursor cursor = database.rawQuery(selectQuery, null);

        User friend = null;
        if (cursor.moveToFirst()) {
            do {
                friend = cursorToFriend(cursor);
            } while (cursor.moveToNext());
        }
        return friend;
    }

    private User cursorToFriend(Cursor cursor) {
        User friend = new User();
        friend.setId(cursor.getString(0));
        friend.setUserName(cursor.getString(1));
        friend.setScore(cursor.getInt(2));
        return friend;
    }

    public Game createGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.GAMES_COLUMN_ID, game.getId());
        values.put(MySQLiteHelper.GAMES_COLUMN_STATE, game.getState().getValue());
        if (game.getActiveRound() != null) {
            values.put(MySQLiteHelper.GAMES_COLUMN_ACTIVE_ROUND, game.getActiveRound().getId());
        } else {
            values.put(MySQLiteHelper.GAMES_COLUMN_ACTIVE_ROUND, "");
        }
        database.insert(MySQLiteHelper.TABLE_GAMES, null, values);
        List<Round> rounds = game.getRounds();
        if (rounds != null) {
            for (Round round : rounds) {
                createRound(round);
            }
        }
        List<Participant> participants = game.getParticipants();
        if (participants != null) {
            for (Participant participant : participants) {
                createParticipant(participant, game.getId());
            }
        }
        return game;
    }

    public void deleteGame(Game game) {
        String id = game.getId();
        System.out.println("Game deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_GAMES, MySQLiteHelper.GAMES_COLUMN_ID
                + " = \"" + id + "\"", null);

        Collection<Round> rounds = game.getRounds();
        for (Round round : rounds) {
            deleteRound(round);
        }
        Collection<Participant> participants = game.getParticipants();
        for (Participant participant : participants) {
            deleteParticipant(participant);
        }
    }

    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<Game>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_GAMES,
                allColumnsGames, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Game game = cursorToGame(cursor);
            game.setRounds(getAllRoundsForGameId(game.getId()));
            if (game.getActiveRound() != null) {
                for (Round round : game.getRounds()) {
                    if (round.getId() == game.getActiveRoundID()) {
                        game.setActiveRound(round);
                    }
                }
            }
            game.setParticipants(getAllParticipantsForGameId(game.getId()));
            games.add(game);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return games;
    }

    public Game updateGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.GAMES_COLUMN_ID, game.getId());
        values.put(MySQLiteHelper.GAMES_COLUMN_STATE, game.getState().getValue());
        values.put(MySQLiteHelper.GAMES_COLUMN_ACTIVE_ROUND, game.getActiveRound().getId());
        database.update(MySQLiteHelper.TABLE_GAMES, values, MySQLiteHelper.GAMES_COLUMN_ID + " = \"" + game.getId() + "\"", null);
        List<Round> rounds = new ArrayList<Round>();
        if (game.getRounds() != null) {
            for (Round round : game.getRounds()) {
                rounds.add(updateRound(round));
            }
        }
        game.setRounds(rounds);
        List<Participant> participants = new ArrayList<Participant>();
        if (game.getParticipants() != null) {
            for (Participant participant : game.getParticipants()) {
                participants.add(updateParticipant(participant));
            }
        }
        game.setParticipants(participants);
        return game;
    }

    public void updateGames(List<Game> newGames) {
        List<Game> oldGames = getAllGames();
        for (Game game : newGames) {
            if (oldGames.contains(game)) {
                updateGame(game);
            } else {
                createGame(game);
            }
        }
        oldGames.removeAll(newGames);
        for (Game game : oldGames) {
            deleteGame(game);
        }
    }

    private Game cursorToGame(Cursor cursor) {
        Game game = new Game();
        game.setId(cursor.getString(0));
        game.setState(GameState.values()[cursor.getInt(1)]);
        game.setActiveRoundID(cursor.getString(2));
        return game;
    }

    public Participant createParticipant(Participant participant, String gameId) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_ID, participant.getId());
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_GAME, gameId);
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_PLAYER, participant.getPlayer());
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_STATE, participant.getState());
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_SCORE, participant.getScore());
        database.insert(MySQLiteHelper.TABLE_PARTICIPANTS, null, values);
        return participant;
    }

    private void deleteParticipant(Participant participant) {
        database.delete(MySQLiteHelper.TABLE_PARTICIPANTS, MySQLiteHelper.PARTICIPANTS_COLUMN_ID
                + " = \"" + participant.getId() + "\"", null);
    }

    private List<Participant> getAllParticipantsForGameId(String id) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PARTICIPANTS + " WHERE "
                + MySQLiteHelper.PARTICIPANTS_COLUMN_GAME + " = \"" + id + "\"";

        Cursor cursor = database.rawQuery(selectQuery, null);

        List<Participant> participants = new ArrayList<Participant>();
        if (cursor.moveToFirst()) {
            do {
                Participant participant = cursorToParticipant(cursor);
                participants.add(participant);
            } while (cursor.moveToNext());
        }
        return participants;
    }

    public Participant updateParticipant(Participant participant) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_ID, participant.getId());
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_PLAYER, participant.getPlayer());
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_STATE, participant.getState());
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_SCORE, participant.getScore());
        database.update(MySQLiteHelper.TABLE_PARTICIPANTS, values, MySQLiteHelper.PARTICIPANTS_COLUMN_ID + " = \"" + participant.getId() + "\"", null);
        return participant;
    }

    private Participant cursorToParticipant(Cursor cursor) {
        Participant participant = new Participant();
        participant.setId(cursor.getString(0));
        participant.setPlayer(cursor.getString(1));
        participant.setState(cursor.getInt(3));
        participant.setScore(cursor.getInt(4));
        return participant;
    }

    public Round createRound(Round round) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.ROUNDS_COLUMN_ID, round.getId());
        values.put(MySQLiteHelper.ROUNDS_COLUMN_CATEGORY, round.getCategory());
        database.insert(MySQLiteHelper.TABLE_ROUNDS, null, values);
        return round;
    }

    private void deleteRound(Round round) {
        database.delete(MySQLiteHelper.TABLE_ROUNDS, MySQLiteHelper.ROUNDS_COLUMN_ID
                + " = \"" + round.getId() + "\"", null);
        Collection<Question> questions = round.getQuestions();
        for (Question question : questions) {
            deleteQuestion(question);
        }
    }

    private List<Round> getAllRoundsForGameId(String id) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_ROUNDS + " WHERE "
                + MySQLiteHelper.ROUNDS_COLUMN_GAME + " = \"" + id + "\"";

        Cursor cursor = database.rawQuery(selectQuery, null);

        List<Round> rounds = new ArrayList<Round>();
        if (cursor.moveToFirst()) {
            do {
                Round round = cursorToRound(cursor);
                round.setQuestions(getAllQuestionsForRoundId(round.getId()));
                rounds.add(round);
            } while (cursor.moveToNext());
        }
        return rounds;
    }

    public Round updateRound(Round round) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.ROUNDS_COLUMN_ID, round.getId());
        values.put(MySQLiteHelper.ROUNDS_COLUMN_CATEGORY, round.getCategory());
        database.update(MySQLiteHelper.TABLE_ROUNDS, values, MySQLiteHelper.ROUNDS_COLUMN_ID + " = \"" + round.getId() + "\"", null);
        List<Question> questions = new ArrayList<Question>();
        if (round.getQuestions() != null) {
            for (Question question : round.getQuestions()) {
                updateQuestion(question);
            }
        }
        round.setQuestions(questions);
        return round;
    }

    private Round cursorToRound(Cursor cursor) {
        Round round = new Round();
        round.setId(cursor.getString(0));
        round.setCategory(cursor.getString(1));
        return round;
    }

    public Question createQuestion(Question question) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ID, question.getId());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_TEXT, question.getText());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LAT, question.getAnswerLat());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LONG, question.getAnswerLong());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_TEXT, question.getAnswerText());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_STATE, question.getState());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_PARTICIPANT_WHO_ONE, question.getParticipantWhoWon());
        database.insert(MySQLiteHelper.TABLE_QUESTIONS, null, values);
        Map<String, Turninformation> turninformation = question.getTurninformation();
        for (Map.Entry<String, Turninformation> turninfo : turninformation.entrySet()) {
            createTurninformation(turninfo.getValue(), question.getId(), turninfo.getKey());
        }
        return question;
    }

    private void deleteQuestion(Question question) {
        database.delete(MySQLiteHelper.TABLE_QUESTIONS, MySQLiteHelper.QUESTIONS_COLUMN_ID
                + " = \"" + question.getId() + "\"", null);
        Map<String, Turninformation> turninformation = question.getTurninformation();
        for (Map.Entry<String, Turninformation> turninfo : turninformation.entrySet()) {
            deleteTurnInformation(turninfo);
        }
    }

    private List<Question> getAllQuestionsForRoundId(String id) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_QUESTIONS + " WHERE "
                + MySQLiteHelper.QUESTIONS_COLUMN_ROUND + " = \"" + id + "\"";

        Cursor cursor = database.rawQuery(selectQuery, null);

        List<Question> questions = new ArrayList<Question>();
        if (cursor.moveToFirst()) {
            do {
                Question question = cursorToQuestion(cursor);
                question.setTurninformation(getAllTurninformationForQuestionID(question.getId()));
                questions.add(question);
            } while (cursor.moveToNext());
        }
        return questions;
    }

    public Question updateQuestion(Question question) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ID, question.getId());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_TEXT, question.getText());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LAT, question.getAnswerLat());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LONG, question.getAnswerLong());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_TEXT, question.getAnswerText());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_STATE, question.getState());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_PARTICIPANT_WHO_ONE, question.getParticipantWhoWon());
        database.update(MySQLiteHelper.TABLE_QUESTIONS, values, MySQLiteHelper.QUESTIONS_COLUMN_ID + " = \"" + question.getId() + "\"", null);
        Map<String, Turninformation> turninformation = question.getTurninformation();
        for (Map.Entry<String, Turninformation> turninfo : turninformation.entrySet()) {
            createTurninformation(turninfo.getValue(), question.getId(), turninfo.getKey());
        }
        return question;
    }

    private Question cursorToQuestion(Cursor cursor) {
        Question question = new Question();
        question.setId(cursor.getString(0));
        question.setText(cursor.getString(1));
        question.setAnswerLat(cursor.getFloat(3));
        question.setAnswerLong(cursor.getFloat(4));
        question.setAnswerText(cursor.getString(5));
        question.setState(cursor.getInt(6));
        question.setParticipantWhoWon(cursor.getString(7));
        return question;
    }

    private Turninformation createTurninformation(Turninformation turninformation, String questionId, String participantId) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_ID, turninformation.getId());
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_QUESTION, questionId);
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_PARTICIPANT, participantId);
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_LAT, turninformation.getAnswerLat());
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_LONG, turninformation.getAnswerLong());
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_DISTANCE, turninformation.getDistance());
        database.insert(MySQLiteHelper.TABLE_TURNINFORMATION, null, values);
        return turninformation;
    }

    private void deleteTurnInformation(Map.Entry<String, Turninformation> turninfo) {
        database.delete(MySQLiteHelper.TABLE_TURNINFORMATION, MySQLiteHelper.TURNINFORMATION_COLUMN_ID
                + " = \"" + turninfo.getValue().getId() + "\"", null);
    }

    private HashMap<String, Turninformation> getAllTurninformationForQuestionID(String id) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_TURNINFORMATION + " WHERE "
                + MySQLiteHelper.TURNINFORMATION_COLUMN_QUESTION + " = \"" + id + "\"";

        Cursor cursor = database.rawQuery(selectQuery, null);

        HashMap<String, Turninformation> turninformation = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                Turninformation info = cursorToTurninformation(cursor);
                turninformation.put(cursor.getString(2), info);
            } while (cursor.moveToNext());
        }
        return turninformation;
    }

    public Turninformation updateTurninformation(Turninformation turninformation, String questionId, String participantId) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_ID, turninformation.getId());
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_QUESTION, questionId);
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_PARTICIPANT, participantId);
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_LAT, turninformation.getAnswerLat());
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_LONG, turninformation.getAnswerLong());
        values.put(MySQLiteHelper.TURNINFORMATION_COLUMN_DISTANCE, turninformation.getDistance());
        database.update(MySQLiteHelper.TABLE_TURNINFORMATION, values, MySQLiteHelper.TURNINFORMATION_COLUMN_ID + " = \"" + turninformation.getId() + "\"", null);
        return turninformation;
    }

    private Turninformation cursorToTurninformation(Cursor cursor) {
        Turninformation turninformation = new Turninformation();
        turninformation.setId(cursor.getString(0));
        turninformation.setAnswerLat(cursor.getFloat(3));
        turninformation.setAnswerLong(cursor.getFloat(4));
        turninformation.setDistance(cursor.getFloat(5));
        return turninformation;
    }

    public void dropAllTables() {
        dbHelper.dropTables(database);
    }
} 
