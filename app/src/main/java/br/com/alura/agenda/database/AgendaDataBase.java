package br.com.alura.agenda.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import br.com.alura.agenda.converter.ConversorCalendar;
import br.com.alura.agenda.database.dao.AlunoDAO;
import br.com.alura.agenda.model.Aluno;

@Database(entities = {Aluno.class}, version = 4, exportSchema = false)
@TypeConverters({ConversorCalendar.class})
public abstract class AgendaDataBase extends RoomDatabase {

    private static final String NOME_BANCO_DE_DADOS = "agenda.db";

    public abstract AlunoDAO getRoomAlunoDAO();

    public static AgendaDataBase getInstance(Context context) {
        return Room.databaseBuilder(context, AgendaDataBase.class, NOME_BANCO_DE_DADOS)
                .allowMainThreadQueries() //Executa na ThreadPrincipal, utilizar só para teste
                .addMigrations(new Migration(1, 2) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("ALTER TABLE aluno ADD COLUMN sobrenome TEXT");
                    }
                }, new Migration(2, 3) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        //Criar nova com as informações desejada
                        database.execSQL("CREATE TABLE IF NOT EXISTS `Aluno_novo` "
                                + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                                + " `nome` TEXT, "
                                + "`telefone` TEXT,"
                                + " `email` TEXT)");

                        //Copiar dados da tabela antiga para a nova
                        database.execSQL("INSERT INTO Aluno_novo (id, nome, telefone, email)"
                                + "SELECT id,nome,telefone,email FROM Aluno");

                        //Remove tabela antiga
                        database.execSQL("DROP TABLE Aluno");

                        //Renomear tabela nova com o nome da tabela antiga
                        database.execSQL("ALTER TABLE Aluno_novo RENAME TO Aluno");
                    }
                }, new Migration(3,4) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("ALTER TABLE Aluno ADD COLUMN momentoDeCadastro INTEGER");

                    }
                })
                .build();
    }
}
