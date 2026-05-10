package com.example.quizhelper.data;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.quizhelper.model.Question;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class QuestionDao_Impl implements QuestionDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Question> __insertAdapterOfQuestion;

  private final EntityDeleteOrUpdateAdapter<Question> __deleteAdapterOfQuestion;

  private final EntityDeleteOrUpdateAdapter<Question> __updateAdapterOfQuestion;

  public QuestionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfQuestion = new EntityInsertAdapter<Question>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `questions` (`id`,`type`,`content`,`options`,`correctAnswer`,`createTime`,`quizName`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getType());
        }
        if (entity.getContent() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getContent());
        }
        if (entity.getOptions() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getOptions());
        }
        if (entity.getCorrectAnswer() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getCorrectAnswer());
        }
        statement.bindLong(6, entity.getCreateTime());
        if (entity.getQuizName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getQuizName());
        }
      }
    };
    this.__deleteAdapterOfQuestion = new EntityDeleteOrUpdateAdapter<Question>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `questions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfQuestion = new EntityDeleteOrUpdateAdapter<Question>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `questions` SET `id` = ?,`type` = ?,`content` = ?,`options` = ?,`correctAnswer` = ?,`createTime` = ?,`quizName` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getType());
        }
        if (entity.getContent() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getContent());
        }
        if (entity.getOptions() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getOptions());
        }
        if (entity.getCorrectAnswer() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getCorrectAnswer());
        }
        statement.bindLong(6, entity.getCreateTime());
        if (entity.getQuizName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getQuizName());
        }
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Question question, final Continuation<? super Long> $completion) {
    if (question == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfQuestion.insertAndReturnId(_connection, question);
    }, $completion);
  }

  @Override
  public Object insertAll(final List<Question> questions,
      final Continuation<? super Unit> $completion) {
    if (questions == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfQuestion.insert(_connection, questions);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object delete(final Question question, final Continuation<? super Unit> $completion) {
    if (question == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfQuestion.handle(_connection, question);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final Question question, final Continuation<? super Unit> $completion) {
    if (question == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfQuestion.handle(_connection, question);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<Question>> getAllQuestions() {
    final String _sql = "SELECT * FROM questions ORDER BY createTime DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"questions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfOptions = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "options");
        final int _columnIndexOfCorrectAnswer = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "correctAnswer");
        final int _columnIndexOfCreateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createTime");
        final int _columnIndexOfQuizName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "quizName");
        final List<Question> _result = new ArrayList<Question>();
        while (_stmt.step()) {
          final Question _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpOptions;
          if (_stmt.isNull(_columnIndexOfOptions)) {
            _tmpOptions = null;
          } else {
            _tmpOptions = _stmt.getText(_columnIndexOfOptions);
          }
          final String _tmpCorrectAnswer;
          if (_stmt.isNull(_columnIndexOfCorrectAnswer)) {
            _tmpCorrectAnswer = null;
          } else {
            _tmpCorrectAnswer = _stmt.getText(_columnIndexOfCorrectAnswer);
          }
          final long _tmpCreateTime;
          _tmpCreateTime = _stmt.getLong(_columnIndexOfCreateTime);
          final String _tmpQuizName;
          if (_stmt.isNull(_columnIndexOfQuizName)) {
            _tmpQuizName = null;
          } else {
            _tmpQuizName = _stmt.getText(_columnIndexOfQuizName);
          }
          _item = new Question(_tmpId,_tmpType,_tmpContent,_tmpOptions,_tmpCorrectAnswer,_tmpCreateTime,_tmpQuizName);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<Question>> getQuestionsByQuizName(final String quizName) {
    final String _sql = "SELECT * FROM questions WHERE quizName = ? ORDER BY createTime DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"questions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (quizName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, quizName);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfOptions = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "options");
        final int _columnIndexOfCorrectAnswer = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "correctAnswer");
        final int _columnIndexOfCreateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createTime");
        final int _columnIndexOfQuizName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "quizName");
        final List<Question> _result = new ArrayList<Question>();
        while (_stmt.step()) {
          final Question _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpOptions;
          if (_stmt.isNull(_columnIndexOfOptions)) {
            _tmpOptions = null;
          } else {
            _tmpOptions = _stmt.getText(_columnIndexOfOptions);
          }
          final String _tmpCorrectAnswer;
          if (_stmt.isNull(_columnIndexOfCorrectAnswer)) {
            _tmpCorrectAnswer = null;
          } else {
            _tmpCorrectAnswer = _stmt.getText(_columnIndexOfCorrectAnswer);
          }
          final long _tmpCreateTime;
          _tmpCreateTime = _stmt.getLong(_columnIndexOfCreateTime);
          final String _tmpQuizName;
          if (_stmt.isNull(_columnIndexOfQuizName)) {
            _tmpQuizName = null;
          } else {
            _tmpQuizName = _stmt.getText(_columnIndexOfQuizName);
          }
          _item = new Question(_tmpId,_tmpType,_tmpContent,_tmpOptions,_tmpCorrectAnswer,_tmpCreateTime,_tmpQuizName);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<String>> getAllQuizNames() {
    final String _sql = "SELECT DISTINCT quizName FROM questions ORDER BY createTime DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"questions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final List<String> _result = new ArrayList<String>();
        while (_stmt.step()) {
          final String _item;
          if (_stmt.isNull(0)) {
            _item = null;
          } else {
            _item = _stmt.getText(0);
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getAllQuestionsList(final Continuation<? super List<Question>> $completion) {
    final String _sql = "SELECT * FROM questions";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfOptions = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "options");
        final int _columnIndexOfCorrectAnswer = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "correctAnswer");
        final int _columnIndexOfCreateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createTime");
        final int _columnIndexOfQuizName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "quizName");
        final List<Question> _result = new ArrayList<Question>();
        while (_stmt.step()) {
          final Question _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpOptions;
          if (_stmt.isNull(_columnIndexOfOptions)) {
            _tmpOptions = null;
          } else {
            _tmpOptions = _stmt.getText(_columnIndexOfOptions);
          }
          final String _tmpCorrectAnswer;
          if (_stmt.isNull(_columnIndexOfCorrectAnswer)) {
            _tmpCorrectAnswer = null;
          } else {
            _tmpCorrectAnswer = _stmt.getText(_columnIndexOfCorrectAnswer);
          }
          final long _tmpCreateTime;
          _tmpCreateTime = _stmt.getLong(_columnIndexOfCreateTime);
          final String _tmpQuizName;
          if (_stmt.isNull(_columnIndexOfQuizName)) {
            _tmpQuizName = null;
          } else {
            _tmpQuizName = _stmt.getText(_columnIndexOfQuizName);
          }
          _item = new Question(_tmpId,_tmpType,_tmpContent,_tmpOptions,_tmpCorrectAnswer,_tmpCreateTime,_tmpQuizName);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object searchByContent(final String keyword,
      final Continuation<? super List<Question>> $completion) {
    final String _sql = "SELECT * FROM questions WHERE content LIKE '%' || ? || '%'";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (keyword == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, keyword);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfOptions = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "options");
        final int _columnIndexOfCorrectAnswer = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "correctAnswer");
        final int _columnIndexOfCreateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createTime");
        final int _columnIndexOfQuizName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "quizName");
        final List<Question> _result = new ArrayList<Question>();
        while (_stmt.step()) {
          final Question _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpOptions;
          if (_stmt.isNull(_columnIndexOfOptions)) {
            _tmpOptions = null;
          } else {
            _tmpOptions = _stmt.getText(_columnIndexOfOptions);
          }
          final String _tmpCorrectAnswer;
          if (_stmt.isNull(_columnIndexOfCorrectAnswer)) {
            _tmpCorrectAnswer = null;
          } else {
            _tmpCorrectAnswer = _stmt.getText(_columnIndexOfCorrectAnswer);
          }
          final long _tmpCreateTime;
          _tmpCreateTime = _stmt.getLong(_columnIndexOfCreateTime);
          final String _tmpQuizName;
          if (_stmt.isNull(_columnIndexOfQuizName)) {
            _tmpQuizName = null;
          } else {
            _tmpQuizName = _stmt.getText(_columnIndexOfQuizName);
          }
          _item = new Question(_tmpId,_tmpType,_tmpContent,_tmpOptions,_tmpCorrectAnswer,_tmpCreateTime,_tmpQuizName);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object searchByQuizNameList(final String quizName,
      final Continuation<? super List<Question>> $completion) {
    final String _sql = "SELECT * FROM questions WHERE quizName = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (quizName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, quizName);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfOptions = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "options");
        final int _columnIndexOfCorrectAnswer = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "correctAnswer");
        final int _columnIndexOfCreateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createTime");
        final int _columnIndexOfQuizName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "quizName");
        final List<Question> _result = new ArrayList<Question>();
        while (_stmt.step()) {
          final Question _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpOptions;
          if (_stmt.isNull(_columnIndexOfOptions)) {
            _tmpOptions = null;
          } else {
            _tmpOptions = _stmt.getText(_columnIndexOfOptions);
          }
          final String _tmpCorrectAnswer;
          if (_stmt.isNull(_columnIndexOfCorrectAnswer)) {
            _tmpCorrectAnswer = null;
          } else {
            _tmpCorrectAnswer = _stmt.getText(_columnIndexOfCorrectAnswer);
          }
          final long _tmpCreateTime;
          _tmpCreateTime = _stmt.getLong(_columnIndexOfCreateTime);
          final String _tmpQuizName;
          if (_stmt.isNull(_columnIndexOfQuizName)) {
            _tmpQuizName = null;
          } else {
            _tmpQuizName = _stmt.getText(_columnIndexOfQuizName);
          }
          _item = new Question(_tmpId,_tmpType,_tmpContent,_tmpOptions,_tmpCorrectAnswer,_tmpCreateTime,_tmpQuizName);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getAllQuizNamesList(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT quizName FROM questions";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final List<String> _result = new ArrayList<String>();
        while (_stmt.step()) {
          final String _item;
          if (_stmt.isNull(0)) {
            _item = null;
          } else {
            _item = _stmt.getText(0);
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getMinCreateTimeByQuizName(final String quizName,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT MIN(createTime) FROM questions WHERE quizName = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (quizName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, quizName);
        }
        final Long _result;
        if (_stmt.step()) {
          final Long _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(0);
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getQuestionCountByQuizName(final String quizName,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM questions WHERE quizName = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (quizName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, quizName);
        }
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteByQuizName(final String quizName,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM questions WHERE quizName = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (quizName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, quizName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
