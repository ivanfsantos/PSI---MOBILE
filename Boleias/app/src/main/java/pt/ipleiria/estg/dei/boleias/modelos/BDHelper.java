package pt.ipleiria.estg.dei.boleias.modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


public class BDHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "boleiasBD";
    private static final int DB_VERSION = 2;
    private final SQLiteDatabase database;


    private static final String TABLE_VIATURAS = "viaturas";
    private static final String TABLE_BOLEIAS = "boleias";

    //viaturas
    public static final String ID_VIATURA = "id";
    public static final String MARCA = "marca";
    public static final String MODELO = "modelo";
    public static final String MATRICULA = "matricula";
    public static final String COR = "cor";
    public static final String PERFIL_ID = "perfil_id";

    //boleias
    public static final String ID_BOLEIA = "id";
    public static final String ORIGEM = "origem";
    public static final String DESTINO = "destino";
    public static final String DATA_HORA = "data_hora";
    public static final String LUGARES_DISPONIVEIS = "lugares_disponiveis";
    public static final String PRECO = "preco";
    public static final String VIATURA_ID = "viatura_id";




    public BDHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        database = getWritableDatabase();
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        String createViaturasTable = "CREATE TABLE " + TABLE_VIATURAS +
                "( " + ID_VIATURA + " INTEGER PRIMARY KEY, " +
                MARCA + " TEXT NOT NULL, " +
                MODELO + " TEXT NOT NULL, " +
                MATRICULA + " TEXT NOT NULL, " +
                COR + " TEXT NOT NULL, " +
                PERFIL_ID + " INTEGER NOT NULL" +
                ");";
        db.execSQL(createViaturasTable);

        String createBoleiasTable = "CREATE TABLE " + TABLE_BOLEIAS +
                "( " + ID_BOLEIA + " INTEGER PRIMARY KEY, " +
                ORIGEM + " TEXT NOT NULL, " +
                DESTINO + " TEXT NOT NULL, " +
                DATA_HORA + " TEXT NOT NULL, " +
                LUGARES_DISPONIVEIS + " INTEGER NOT NULL, " +
                PRECO + " INTEGER NOT NULL, " +
                VIATURA_ID + " INTEGER NOT NULL" +

                ");";
        db.execSQL(createBoleiasTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIATURAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOLEIAS);
        this.onCreate(db);
    }


    //crud viaturas
    public Viatura adicionarViaturaBD(Viatura viatura){
        ContentValues values = new ContentValues();

        values.put(ID_VIATURA, viatura.getId());
        values.put(MARCA,viatura.getMarca());
        values.put(MODELO,viatura.getModelo());
        values.put(MATRICULA,viatura.getMatricula());
        values.put(COR,viatura.getCor());
        values.put(PERFIL_ID, viatura.getPerfil_id());

        long id = this.database.insert(TABLE_VIATURAS, null, values);
        if (id > -1){
            return viatura;
        }
        return null;
    }


    public ArrayList<Viatura> getAllViaturasBD(){
        ArrayList<Viatura> viaturas = new ArrayList<>();
        Cursor cursor = this.database.query(TABLE_VIATURAS, new String[]{ ID_VIATURA, MARCA, MODELO, MATRICULA, COR, PERFIL_ID}, null, null, null, null, null, null);
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
        return this.database.update(TABLE_VIATURAS, values,
                "id = ?", new String[]{"" + viatura.getId()}) > 0;
    }

    public boolean removerViaturaBD(long id){
        int affectedRows =  this.database.delete(TABLE_VIATURAS, "id = ?",
                new String[]{"" + id});

        return affectedRows > 0;
    }

    public void removerAllViaturasBD() {
        this.database.delete(TABLE_VIATURAS, null, null);
    }

    //crud boleias
    public Boleia adicionarBoleiaBD(Boleia boleia){
        ContentValues values = new ContentValues();

        values.put(ID_BOLEIA, boleia.getId());
        values.put(ORIGEM,boleia.getOrigem());
        values.put(DESTINO,boleia.getDestino());
        values.put(DATA_HORA,boleia.getData_hora());
        values.put(LUGARES_DISPONIVEIS,boleia.getLugares_disponiveis());
        values.put(PRECO, boleia.getPreco());
        values.put(VIATURA_ID, boleia.getViatura_id());

        long id = this.database.insert(TABLE_BOLEIAS, null, values);
        if (id > -1){
            return boleia;
        }
        return null;
    }


    public ArrayList<Boleia> getAllBoleiasBD(){
        ArrayList<Boleia> boleias = new ArrayList<>();
        Cursor cursor = this.database.query(TABLE_BOLEIAS, new String[]{ ID_BOLEIA, ORIGEM, DESTINO, DATA_HORA, LUGARES_DISPONIVEIS, PRECO, VIATURA_ID}, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                boleias.add(new Boleia(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getInt(6)));
            }while(cursor.moveToNext());
            cursor.close();
        }
        return boleias;
    }

    public boolean editarBoleiaBD(Boleia boleia){

        ContentValues values = new ContentValues();

        values.put(ORIGEM,boleia.getOrigem());
        values.put(DESTINO,boleia.getDestino());
        values.put(DATA_HORA,boleia.getData_hora());
        values.put(LUGARES_DISPONIVEIS,boleia.getLugares_disponiveis());
        values.put(PRECO, boleia.getPreco());
        values.put(VIATURA_ID, boleia.getViatura_id());

        return this.database.update(TABLE_BOLEIAS, values,
                "id = ?", new String[]{"" + boleia.getId()}) > 0;
    }

    public boolean removerBoleiaBD(long id){
        int affectedRows =  this.database.delete(TABLE_BOLEIAS, "id = ?",
                new String[]{"" + id});

        return affectedRows > 0;
    }

    public void removerAllBoleiasBD() {
        this.database.delete(TABLE_BOLEIAS, null, null);
    }
}


