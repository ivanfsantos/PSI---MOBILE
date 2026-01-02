package pt.ipleiria.estg.dei.boleias.modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


public class BDHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "boleiasBD";
    private static final int DB_VERSION = 1;
    private final SQLiteDatabase database;


    private static final String TABLE_NAME = "viaturas";
    public static final String ID = "id";
    public static final String MARCA = "marca";
    public static final String MODELO = "modelo";
    public static final String MATRICULA = "matricula";
    public static final String COR = "cor";
    public static final String PERFIL_ID = "perfil_id";

    public BDHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createViaturasTable = "CREATE TABLE " + TABLE_NAME +
                "( " + ID + " INTEGER PRIMARY KEY, " +
                MARCA + " TEXT NOT NULL, " +
                MODELO + " TEXT NOT NULL, " +
                MATRICULA + " TEXT NOT NULL, " +
                COR + " INTEGER NOT NULL, " +
                PERFIL_ID + " INTEGER NOT NULL" +
                ");";
        db.execSQL(createViaturasTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public Viatura adicionarViaturaBD(Viatura viatura){
        ContentValues values = new ContentValues();

        values.put(MARCA,viatura.getMarca());
        values.put(MODELO,viatura.getModelo());
        values.put(MATRICULA,viatura.getMatricula());
        values.put(COR,viatura.getCor());
        values.put(PERFIL_ID, viatura.getPerfil_id());

        long id = this.database.insert(TABLE_NAME, null, values);
        if (id > -1){
            return viatura;
        }
        return null;
    }

    public ArrayList<Viatura> getAllViaturasBD(){
        ArrayList<Viatura> viaturas = new ArrayList<>();
        Cursor cursor = this.database.query(TABLE_NAME, new String[]{ ID, MARCA, MODELO, MATRICULA, COR, PERFIL_ID}, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                viaturas.add(new Viatura(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5)));
            }while(cursor.moveToNext());
            cursor.close();
        }
        return viaturas;
    }

    public boolean editarViaturaBD(Viatura viatura){
        ContentValues values = new ContentValues();

        values.put(MARCA,viatura.getMarca());
        values.put(MODELO,viatura.getModelo());
        values.put(MATRICULA,viatura.getMatricula());
        values.put(COR,viatura.getCor());
        values.put(PERFIL_ID, viatura.getPerfil_id());
        return this.database.update(TABLE_NAME, values,
                "id = ?", new String[]{"" + viatura.getId()}) > 0;
    }

    public boolean removerViaturaBD(long id){
        int affectedRows =  this.database.delete(TABLE_NAME, "id = ?",
                new String[]{"" + id});

        return affectedRows > 0;
    }

    public void removerAllViaturasBD() {
        this.database.delete(TABLE_NAME, null, null);
    }
}


