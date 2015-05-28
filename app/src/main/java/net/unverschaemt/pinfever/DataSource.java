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
            MySQLiteHelper.FRIENDS_COLUMN_USERNAME, MySQLiteHelper.FRIENDS_COLUMN_SCORE,
            MySQLiteHelper.FRIENDS_COLUMN_AVATAR};
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
        values.put(MySQLiteHelper.FRIENDS_COLUMN_AVATAR, friend.getAvatar());
        database.insert(MySQLiteHelper.TABLE_FRIENDS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumnsFriends, MySQLiteHelper.FRIENDS_COLUMN_ID + " = \"" + friend.getId() + "\"", null,
                null, null, null);
        cursor.moveToFirst();
        User newUser = cursorToFriend(cursor);
        cursor.close();
        return newUser;
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

    private User cursorToFriend(Cursor cursor) {
        User friend = new User();
        friend.setId(cursor.getString(0));
        friend.setUserName(cursor.getString(1));
        friend.setScore(cursor.getInt(2));
        friend.setAvatar(cursor.getInt(3));
        return friend;
    }

    public Game updateGame(GameDTO game) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.GAMES_COLUMN_ID, game.id);
        values.put(MySQLiteHelper.GAMES_COLUMN_STATE, game.state);
        values.put(MySQLiteHelper.GAMES_COLUMN_ACTIVE_ROUND, game.activeRound);
        database.update(MySQLiteHelper.TABLE_GAMES, values, MySQLiteHelper.GAMES_COLUMN_ID + " = " + game.id, null);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_GAMES,
                allColumnsGames, MySQLiteHelper.GAMES_COLUMN_ID + " = " + game.id, null,
                null, null, null);
        cursor.moveToFirst();
        Game newGame = cursorToGame(cursor);
        cursor.close();
        List<Round> rounds = new ArrayList<Round>();
        if (game.rounds != null) {
            for (RoundDTO round : game.rounds) {
                rounds.add(updateRound(round));
            }
        }
        newGame.setRounds(rounds);
        List<Participant> participants = new ArrayList<Participant>();
        if (game.participants != null) {
            for (ParticipantDTO participant : game.participants) {
                participants.add(updateParticipant(participant));
            }
        }
        newGame.setParticipants(participants);
        return newGame;
    }

    public Game updateGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.GAMES_COLUMN_ID, game.getId());
        values.put(MySQLiteHelper.GAMES_COLUMN_STATE, game.getState().getValue());
        values.put(MySQLiteHelper.GAMES_COLUMN_ACTIVE_ROUND, game.getActiveRound().getId());
        database.update(MySQLiteHelper.TABLE_GAMES, values, MySQLiteHelper.GAMES_COLUMN_ID + " = " + game.getId(), null);
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

    public void deleteGame(Game game) {
        long id = game.getId();
        System.out.println("Game deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_GAMES, MySQLiteHelper.GAMES_COLUMN_ID
                + " = " + id, null);

        Collection<Round> rounds = game.getRounds();
        for (Round round : rounds) {
            database.delete(MySQLiteHelper.TABLE_ROUNDS, MySQLiteHelper.ROUNDS_COLUMN_ID
                    + " = " + round.getId(), null);
            Collection<Question> questions = round.getQuestions();
            for (Question question : questions) {
                database.delete(MySQLiteHelper.TABLE_QUESTIONS, MySQLiteHelper.QUESTIONS_COLUMN_ID
                        + " = " + question.getId(), null);
                java.util.Map<Long, Turninformation> turninformation = question.getTurninformation();
                for (Map.Entry<Long, Turninformation> turninfo : turninformation.entrySet()) {
                    database.delete(MySQLiteHelper.TABLE_TURNINFORMATION, MySQLiteHelper.TURNINFORMATION_COLUMN_ID
                            + " = " + turninfo.getValue().getId(), null);
                }
            }
        }
        Collection<Participant> participants = game.getParticipants();
        for (Participant participant : participants) {
            database.delete(MySQLiteHelper.TABLE_PARTICIPANTS, MySQLiteHelper.PARTICIPANTS_COLUMN_ID
                    + " = " + participant.getId(), null);
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
            if (game.getActiveRoundID() > -1) {
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

    private List<Participant> getAllParticipantsForGameId(long id) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PARTICIPANTS + " WHERE "
                + MySQLiteHelper.PARTICIPANTS_COLUMN_GAME + " = " + id;

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

    public Participant createParticipant(ParticipantDTO participant) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_ID, participant.id);
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_PLAYER, participant.player);
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_GAME, participant.game);
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_STATE, participant.state);
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_SCORE, participant.score);
        database.insert(MySQLiteHelper.TABLE_PARTICIPANTS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PARTICIPANTS,
                allColumnsParticipants, MySQLiteHelper.PARTICIPANTS_COLUMN_ID + " = " + participant.id, null,
                null, null, null);
        cursor.moveToFirst();
        Participant newParticipant = cursorToParticipant(cursor);
        cursor.close();
        return newParticipant;
    }

    public Participant updateParticipant(ParticipantDTO participant) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_ID, participant.id);
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_PLAYER, participant.player);
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_GAME, participant.game);
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_STATE, participant.state);
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_SCORE, participant.score);
        database.update(MySQLiteHelper.TABLE_PARTICIPANTS, values, MySQLiteHelper.PARTICIPANTS_COLUMN_ID + " = " + participant.id, null);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PARTICIPANTS,
                allColumnsParticipants, MySQLiteHelper.PARTICIPANTS_COLUMN_ID + " = " + participant.id, null,
                null, null, null);
        cursor.moveToFirst();
        Participant newParticipant = cursorToParticipant(cursor);
        cursor.close();
        return newParticipant;
    }

    public Participant updateParticipant(Participant participant) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_ID, participant.getId());
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_PLAYER, participant.getPlayer());
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_STATE, participant.getState());
        values.put(MySQLiteHelper.PARTICIPANTS_COLUMN_SCORE, participant.getScore());
        database.update(MySQLiteHelper.TABLE_PARTICIPANTS, values, MySQLiteHelper.PARTICIPANTS_COLUMN_ID + " = " + participant.getId(), null);
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

    private List<Round> getAllRoundsForGameId(long id) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_ROUNDS + " WHERE "
                + MySQLiteHelper.ROUNDS_COLUMN_GAME + " = " + id;

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

    private List<Question> getAllQuestionsForRoundId(long id) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_QUESTIONS + " WHERE "
                + MySQLiteHelper.QUESTIONS_COLUMN_ROUND + " = " + id;

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

    private HashMap<Long, Turninformation> getAllTurninformationForQuestionID(long id) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_TURNINFORMATION + " WHERE "
                + MySQLiteHelper.TURNINFORMATION_COLUMN_QUESTION + " = " + id;

        Cursor cursor = database.rawQuery(selectQuery, null);

        HashMap<Long, Turninformation> turninformation = new HashMap<Long, Turninformation>();
        if (cursor.moveToFirst()) {
            do {
                Turninformation info = cursorToTurninformation(cursor);
                turninformation.put(cursor.getLong(2), info);
            } while (cursor.moveToNext());
        }
        return turninformation;
    }

    private Turninformation cursorToTurninformation(Cursor cursor) {
        Turninformation turninformation = new Turninformation();
        turninformation.setId(cursor.getLong(0));
        turninformation.setAnswerLat(cursor.getFloat(3));
        turninformation.setAnswerLong(cursor.getFloat(4));
        turninformation.setDistance(cursor.getFloat(5));
        return turninformation;
    }

    private Game cursorToGame(Cursor cursor) {
        Game game = new Game();
        game.setID(cursor.getLong(0));
        game.setState(GameState.values()[cursor.getInt(1)]);
        game.setActiveRoundID(cursor.getLong(2));
        return game;
    }

    public Question createQuestion(QuestionDTO question) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ID, question.id);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_TEXT, question.text);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ROUND, question.round);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LAT, question.answerLat);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LONG, question.answerLong);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_STATE, question.state);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_PARTICIPANT_WHO_ONE, question.participantWhoWon);
        database.insert(MySQLiteHelper.TABLE_QUESTIONS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS,
                allColumnsQuestions, MySQLiteHelper.QUESTIONS_COLUMN_ID + " = " + question.id, null,
                null, null, null);
        cursor.moveToFirst();
        Question newQuestion = cursorToQuestion(cursor);
        cursor.close();
        return newQuestion;
    }

    public Question updateQuestion(QuestionDTO question) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ID, question.id);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_TEXT, question.text);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ROUND, question.round);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LAT, question.answerLat);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LONG, question.answerLong);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_STATE, question.state);
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_PARTICIPANT_WHO_ONE, question.participantWhoWon);
        database.update(MySQLiteHelper.TABLE_QUESTIONS, values, MySQLiteHelper.QUESTIONS_COLUMN_ID + " = " + question.id, null);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS,
                allColumnsQuestions, MySQLiteHelper.QUESTIONS_COLUMN_ID + " = " + question.id, null,
                null, null, null);
        cursor.moveToFirst();
        Question newQuestion = cursorToQuestion(cursor);
        cursor.close();
        return newQuestion;
    }

    public Question updateQuestion(Question question) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ID, question.getId());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_TEXT, question.getText());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LAT, question.getAnswerLat());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_ANSWER_LONG, question.getAnswerLong());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_STATE, question.getState());
        values.put(MySQLiteHelper.QUESTIONS_COLUMN_PARTICIPANT_WHO_ONE, question.getParticipantWhoWon());
        database.update(MySQLiteHelper.TABLE_QUESTIONS, values, MySQLiteHelper.QUESTIONS_COLUMN_ID + " = " + question.getId(), null);
        return question;
    }

    private Question cursorToQuestion(Cursor cursor) {
        Question question = new Question();
        question.setId(cursor.getLong(0));
        question.setText(cursor.getString(1));
        question.setAnswerLat(cursor.getFloat(3));
        question.setAnswerLong(cursor.getFloat(4));
        question.setState(cursor.getInt(5));
        question.setParticipantWhoWon(cursor.getInt(6));
        return question;
    }

    public Round createRound(RoundDTO round) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.ROUNDS_COLUMN_ID, round.id);
        values.put(MySQLiteHelper.ROUNDS_COLUMN_CATEGORY, round.category);
        values.put(MySQLiteHelper.ROUNDS_COLUMN_GAME, round.game);
        database.insert(MySQLiteHelper.TABLE_ROUNDS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ROUNDS,
                allColumnsRounds, MySQLiteHelper.ROUNDS_COLUMN_ID + " = " + round.id, null,
                null, null, null);
        cursor.moveToFirst();
        Round newRound = cursorToRound(cursor);
        cursor.close();
        List<Question> questions = new ArrayList<Question>();
        if (round.questions != null) {
            for (QuestionDTO question : round.questions) {
                questions.add(createQuestion(question));
            }
        }
        newRound.setQuestions(questions);
        return newRound;
    }

    public Round updateRound(RoundDTO round) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.ROUNDS_COLUMN_ID, round.id);
        values.put(MySQLiteHelper.ROUNDS_COLUMN_CATEGORY, round.category);
        values.put(MySQLiteHelper.ROUNDS_COLUMN_GAME, round.game);
        database.update(MySQLiteHelper.TABLE_ROUNDS, values, MySQLiteHelper.ROUNDS_COLUMN_ID + " = " + round.id, null);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ROUNDS,
                allColumnsRounds, MySQLiteHelper.ROUNDS_COLUMN_ID + " = " + round.id, null,
                null, null, null);
        cursor.moveToFirst();
        Round newRound = cursorToRound(cursor);
        cursor.close();
        List<Question> questions = new ArrayList<Question>();
        if (round.questions != null) {
            for (QuestionDTO question : round.questions) {
                questions.add(updateQuestion(question));
            }
        }
        newRound.setQuestions(questions);
        return newRound;
    }

    public Round updateRound(Round round) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.ROUNDS_COLUMN_ID, round.getId());
        values.put(MySQLiteHelper.ROUNDS_COLUMN_CATEGORY, round.getCategory());
        database.update(MySQLiteHelper.TABLE_ROUNDS, values, MySQLiteHelper.ROUNDS_COLUMN_ID + " = " + round.getId(), null);
        List<Question> questions = new ArrayList<Question>();
        if (round.getQuestions() != null) {
            for (Question question : round.getQuestions()) {
                questions.add(updateQuestion(question));
            }
        }
        round.setQuestions(questions);
        return round;
    }

    private Round cursorToRound(Cursor cursor) {
        Round round = new Round();
        round.setId(cursor.getLong(0));
        round.setCategory(cursor.getString(1));
        return round;
    }

    public void updateFriends(List<User> newFriends) {
        List<User> oldFriends = getAllFriends();
        for (User friend : newFriends) {
            updateFriend(friend);

        }
        oldFriends.removeAll(newFriends);
        for (User friend : oldFriends) {
            deleteFriend(friend);
        }
    }

    private void updateFriend(User friend) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.FRIENDS_COLUMN_ID, friend.getId());
        values.put(MySQLiteHelper.FRIENDS_COLUMN_USERNAME, friend.getUserName());
        values.put(MySQLiteHelper.FRIENDS_COLUMN_SCORE, friend.getScore());
        values.put(MySQLiteHelper.FRIENDS_COLUMN_AVATAR, friend.getAvatar());
        database.update(MySQLiteHelper.TABLE_FRIENDS, values, MySQLiteHelper.FRIENDS_COLUMN_ID + " = " + friend.getId(), null);
    }
} 
