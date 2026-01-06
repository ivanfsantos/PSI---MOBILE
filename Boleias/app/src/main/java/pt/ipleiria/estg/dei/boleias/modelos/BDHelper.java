package pt.ipleiria.estg.dei.boleias.modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


public class BDHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "boleiasBD";
    private static final int DB_VERSION = 3;
    private final SQLiteDatabase database;

    //tabelas
    private static final String TABLE_VIATURAS = "viaturas";
    private static final String TABLE_BOLEIAS = "boleias";
    private static final String TABLE_RESERVAS = "reservas";

    //campos
    public static final String ID = "id";
    public static final String MARCA = "marca";
    public static final String MODELO = "modelo";
    public static final String MATRICULA = "matricula";
    public static final String COR = "cor";
    public static final String PERFIL_ID = "perfil_id";
    public static final String ORIGEM = "origem";
    public static final String DESTINO = "destino";
    public static final String DATA_HORA = "data_hora";
    public static final String LUGARES_DISPONIVEIS = "lugares_disponiveis";
    public static final String PRECO = "preco";
    public static final String VIATURA_ID = "viatura_id";
    public static final String PONTO_ENCONTRO = "ponto_encontro";
    public static final String CONTACTO = "contacto";
    public static final String REEMBOLSO = "reembolso";
    public static final String ESTADO = "estado";
    public static final String BOLEIA_ID = "boleia_id";




    public BDHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        database = getWritableDatabase();
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        String createViaturasTable = "CREATE TABLE " + TABLE_VIATURAS +
                "( " + ID + " INTEGER PRIMARY KEY, " +
                MARCA + " TEXT NOT NULL, " +
                MODELO + " TEXT NOT NULL, " +
                MATRICULA + " TEXT NOT NULL, " +
                COR + " TEXT NOT NULL, " +
                PERFIL_ID + " INTEGER NOT NULL" +
                ");";
        db.execSQL(createViaturasTable);

        String createBoleiasTable = "CREATE TABLE " + TABLE_BOLEIAS +
                "( " + ID + " INTEGER PRIMARY KEY, " +
                ORIGEM + " TEXT NOT NULL, " +
                DESTINO + " TEXT NOT NULL, " +
                DATA_HORA + " TEXT NOT NULL, " +
                LUGARES_DISPONIVEIS + " INTEGER NOT NULL, " +
                PRECO + " DOUBLE NOT NULL, " +
                VIATURA_ID + " INTEGER NOT NULL" +

                ");";
        db.execSQL(createBoleiasTable);

        String createReservasTable = "CREATE TABLE " + TABLE_RESERVAS +
                "( " + ID + " INTEGER PRIMARY KEY, " +
                PONTO_ENCONTRO + " TEXT NOT NULL, " +
                CONTACTO + " INTEGER NOT NULL, " +
                REEMBOLSO + " DOUBLE NOT NULL, " +
                ESTADO + " TEXT NOT NULL, " +
                PERFIL_ID + " INTEGER NOT NULL, " +
                BOLEIA_ID + " INTEGER NOT NULL" +

                ");";
        db.execSQL(createReservasTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIATURAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOLEIAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVAS);

        this.onCreate(db);
    }


    //crud viaturas
    public Viatura adicionarViaturaBD(Viatura viatura){
        ContentValues values = new ContentValues();

        values.put(ID, viatura.getId());
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
        Cursor cursor = this.database.query(TABLE_VIATURAS, new String[]{ ID, MARCA, MODELO, MATRICULA, COR, PERFIL_ID}, null, null, null, null, null, null);
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

        values.put(ID, boleia.getId());
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
        Cursor cursor = this.database.query(TABLE_BOLEIAS, new String[]{ ID, ORIGEM, DESTINO, DATA_HORA, LUGARES_DISPONIVEIS, PRECO, VIATURA_ID}, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                boleias.add(new Boleia(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getDouble(5),
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

    //crud reservas
    public Reserva adicionarReservaBD(Reserva reserva){
        ContentValues values = new ContentValues();

        values.put(ID, reserva.getId());
        values.put(PONTO_ENCONTRO,reserva.getPonto_encontro());
        values.put(CONTACTO,reserva.getContacto());
        values.put(REEMBOLSO,reserva.getReembolso());
        values.put(ESTADO,reserva.getEstado());
        values.put(PERFIL_ID, reserva.getPerfil_id());
        values.put(BOLEIA_ID, reserva.getBoleia_id());

        long id = this.database.insert(TABLE_RESERVAS, null, values);
        if (id > -1){
            return reserva;
        }
        return null;
    }


    public ArrayList<Reserva> getAllReservasBD(){
        ArrayList<Reserva> reservas = new ArrayList<>();
        Cursor cursor = this.database.query(TABLE_RESERVAS, new String[]{ ID, PONTO_ENCONTRO, CONTACTO, REEMBOLSO, ESTADO, PERFIL_ID, BOLEIA_ID}, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                reservas.add(new Reserva(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getDouble(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getInt(6)
                        ));

            }while(cursor.moveToNext());
            cursor.close();
        }
        return reservas;
    }


    public boolean removerReservaBD(long id){
        int affectedRows =  this.database.delete(TABLE_RESERVAS, "id = ?",
                new String[]{"" + id});

        return affectedRows > 0;
    }

    public void removerAllReservasBD() {
        this.database.delete(TABLE_RESERVAS, null, null);
    }
}


