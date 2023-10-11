package sn.ept.git.mobile.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import sn.ept.git.mobile.api.models.categorie.Categorie;
import sn.ept.git.mobile.api.models.magasin.Magasin;
import sn.ept.git.mobile.api.models.marque.Marque;
import sn.ept.git.mobile.api.models.produit.Produit;
import sn.ept.git.mobile.api.models.stock.NewStockRequest;
import sn.ept.git.mobile.api.models.stock.Stock;
import sn.ept.git.mobile.api.models.stock.StockID;

public class Handler extends SQLiteOpenHelper {
    private static final String DB_NAME = "vente_velos";
    private static final int DB_VERSION = 1;
    // TABLE STOCK
    private static final String STOCK_TABLE_NAME = "stock";
    private static final String MAGASIN_ID_COL = "magasin_id";
    private static final String PRODUIT_ID_COL = "produit_id";
    private static final String QUANTITE_COL = "quantite";
    // TABLE MAGASIN
    private static final String MAGASIN_TABLE_NAME = "magasin";
    private static final String ID_COL = "id";
    private static final String NOM_COL = "nom";
    private static final String ADRESSE_COL = "adresse";
    private static final String CODE_ZIP_COL = "code_zip";
    private static final String EMAIL_COL = "email";
    private static final String ETAT_COL = "etat";
    private static final String TELEPHONE_COL = "telephone";
    private static final String VILLE_COL = "ville";
    // TABLE MARQUE
    private static final String MARQUE_TABLE_NAME = "marque";
    // TABLE CATEGORIE
    private static final String CATEGORIE_TABLE_NAME = "categorie";
    // TABLE PRODUIT
    private static final String PRODUCT_TABLE_NAME = "produit";
    private static final String MARQUE_ID_COL = "marque_id";
    private static final String CATEGORIE_ID_COL = "categorie_id";
    private static final String ANNEE_MODEL_COL = "annee_model";
    private static final String PRIX_DEPART_COL = "prix_depart";
    private SQLiteDatabase db;

    public Handler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTableMarque(sqLiteDatabase);
        createTableCategorie(sqLiteDatabase);
        createTableMagasin(sqLiteDatabase);
        createTableProduit(sqLiteDatabase);
        createTableStock(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MARQUE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORIE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MAGASIN_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + STOCK_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public Object addStock(NewStockRequest newStockRequest) {
        Produit produit = findProduct(newStockRequest.getProductName());
        Magasin magasin = findMagasin(newStockRequest.getMagasinName());
        if (produit == null || magasin == null) {
            return "Stock with given Produit or Magasin do not Exist";
        }
        Stock stock = findStock(new StockID(produit.getId(), magasin.getId()));
        if (stock != null) {
            return "Stock already Exist!";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("MAGASIN_ID", magasin.getId());
        values.put("PRODUIT_ID", produit.getId());
        values.put("QUANTITE", newStockRequest.getQuantite());

        db.insert("stock", null, values);
        db.close();

        stock = new Stock();
        stock.setId(new StockID(magasin.getId(), produit.getId()));
        stock.setQuantite(newStockRequest.getQuantite());
        stock.setProduit(produit);
        stock.setMagasin(magasin);

        return stock;
    }

    private Produit findProduct(String produitName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Produit produit = null;

        String selectQuery = "SELECT * FROM produit WHERE NOM = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{produitName});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(ID_COL));
            @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(NOM_COL));
            @SuppressLint("Range") int marqueId = cursor.getInt(cursor.getColumnIndex(MARQUE_ID_COL));
            @SuppressLint("Range") int categorieId = cursor.getInt(cursor.getColumnIndex(CATEGORIE_ID_COL));
            @SuppressLint("Range") int anneeModel = cursor.getInt(cursor.getColumnIndex(ANNEE_MODEL_COL));
            @SuppressLint("Range") double prixDepart = cursor.getDouble(cursor.getColumnIndex(PRIX_DEPART_COL));

            produit = new Produit();
            produit.setId(id);
            produit.setNom(nom);
            produit.setMarqueId(marqueId);
            produit.setCategorieId(categorieId);
            produit.setPrixDepart(prixDepart);
            produit.setAnneeModel(anneeModel);

            // You may also want to set the associated Marque and Categorie objects here

            cursor.close();
        }
        db.close();

        return produit;
    }

    private Magasin findMagasin(String magasinName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Magasin magasin = null;

        String selectQuery = "SELECT * FROM magasin WHERE NOM = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{magasinName});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(ID_COL));
            @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(NOM_COL));
            @SuppressLint("Range") String adresse = cursor.getString(cursor.getColumnIndex(ADRESSE_COL));
            @SuppressLint("Range") String codeZip = cursor.getString(cursor.getColumnIndex(CODE_ZIP_COL));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(EMAIL_COL));
            @SuppressLint("Range") String etat = cursor.getString(cursor.getColumnIndex(ETAT_COL));
            @SuppressLint("Range") String telephone = cursor.getString(cursor.getColumnIndex(TELEPHONE_COL));
            @SuppressLint("Range") String ville = cursor.getString(cursor.getColumnIndex(VILLE_COL));

            magasin = new Magasin();
            magasin.setId(id);
            magasin.setNom(nom);
            magasin.setAdresse(adresse);
            magasin.setCodeZip(codeZip);
            magasin.setEmail(email);
            magasin.setEtat(etat);
            magasin.setTelephone(telephone);
            magasin.setVille(ville);

            cursor.close();
        }
        db.close();

        return magasin;
    }

    public Stock findStock(StockID stockID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Stock stock = null;

        String selectQuery = "SELECT * FROM stock WHERE MAGASIN_ID = ? AND PRODUIT_ID = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(stockID.getMagasinId()), String.valueOf(stockID.getProduitId())});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int magasinId = cursor.getInt(cursor.getColumnIndex("MAGASIN_ID"));
            @SuppressLint("Range") int produitId = cursor.getInt(cursor.getColumnIndex("PRODUIT_ID"));
            @SuppressLint("Range") int quantite = cursor.getInt(cursor.getColumnIndex("QUANTITE"));

            stock = new Stock();
            StockID id = new StockID(magasinId, produitId);
            stock.setId(id);
            stock.setQuantite(quantite);

            cursor.close();
        }
        db.close();

        return stock;
    }

    public void updateStock(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("quantite", stock.getQuantite());

        String whereClause = "magasin_id = ? AND produit_id = ?";
        String[] whereArgs = {String.valueOf(stock.getMagasin().getId()), String.valueOf(stock.getProduit().getId())};

        db.update("stock", values, whereClause, whereArgs);
        db.close();
    }

    public int deleteStock(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "magasin_id = ? AND produit_id = ?";
        String[] whereArgs = {String.valueOf(stock.getMagasin().getId()), String.valueOf(stock.getProduit().getId())};

        int rowsDeleted = db.delete("stock", whereClause, whereArgs);
        db.close();
        return rowsDeleted;
    }

    public List<Stock> getAllStocks() {
        List<Stock> stockList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from stock", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int quantite = cursor.getInt(cursor.getColumnIndex(QUANTITE_COL));
                    @SuppressLint("Range") int produitId = cursor.getInt(cursor.getColumnIndex(PRODUIT_ID_COL));
                    @SuppressLint("Range") int magasinId = cursor.getInt(cursor.getColumnIndex(MAGASIN_ID_COL));

                    Stock stock = new Stock();
                    stock.setQuantite(quantite);

                    Produit produit = getProduitByID(produitId);
                    Magasin magasin = getMagasinByID(magasinId);

                    stock.setProduit(produit);
                    stock.setMagasin(magasin);

                    stockList.add(stock);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return stockList;
    }

    public List<Magasin> getAllMagasins() {
        List<Magasin> magasinList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from magasin", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(ID_COL));
                    @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(NOM_COL));
                    @SuppressLint("Range") String telephone = cursor.getString(cursor.getColumnIndex(TELEPHONE_COL));
                    @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(EMAIL_COL));
                    @SuppressLint("Range") String adresse = cursor.getString(cursor.getColumnIndex(ADRESSE_COL));
                    @SuppressLint("Range") String ville = cursor.getString(cursor.getColumnIndex(VILLE_COL));
                    @SuppressLint("Range") String etat = cursor.getString(cursor.getColumnIndex(ETAT_COL));
                    @SuppressLint("Range") String codeZip = cursor.getString(cursor.getColumnIndex(CODE_ZIP_COL));

                    Magasin magasin = new Magasin();
                    magasin.setId(id);
                    magasin.setNom(nom);
                    magasin.setTelephone(telephone);
                    magasin.setEmail(email);
                    magasin.setAdresse(adresse);
                    magasin.setVille(ville);
                    magasin.setEtat(etat);
                    magasin.setCodeZip(codeZip);

                    magasinList.add(magasin);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return magasinList;
    }

    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from produit", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(ID_COL));
                    @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(NOM_COL));
                    @SuppressLint("Range") int marqueId = cursor.getInt(cursor.getColumnIndex(MARQUE_ID_COL));
                    @SuppressLint("Range") int categorieId = cursor.getInt(cursor.getColumnIndex(CATEGORIE_ID_COL));
                    @SuppressLint("Range") int anneeModel = cursor.getInt(cursor.getColumnIndex(ANNEE_MODEL_COL));
                    @SuppressLint("Range") double prixDepart = cursor.getDouble(cursor.getColumnIndex(PRIX_DEPART_COL));

                    Produit produit = new Produit();
                    produit.setId(id);
                    produit.setNom(nom);
                    produit.setMarqueId(marqueId);
                    produit.setCategorieId(categorieId);
                    produit.setPrixDepart(prixDepart);
                    produit.setAnneeModel(anneeModel);

                    Marque marque = getMarqueByID(marqueId);
                    Categorie categorie = getCategorieByID(categorieId);

                    produit.setMarque(marque);
                    produit.setCategorie(categorie);

                    produits.add(produit);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return produits;
    }

    public Produit getProduitByID(int produitID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Produit produit = null;

        String selectQuery = "SELECT * FROM produit WHERE ID = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(produitID)});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(ID_COL));
            @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(NOM_COL));
            @SuppressLint("Range") int marqueId = cursor.getInt(cursor.getColumnIndex(MARQUE_ID_COL));
            @SuppressLint("Range") int categorieId = cursor.getInt(cursor.getColumnIndex(CATEGORIE_ID_COL));
            @SuppressLint("Range") int anneeModel = cursor.getInt(cursor.getColumnIndex(ANNEE_MODEL_COL));
            @SuppressLint("Range") double prixDepart = cursor.getDouble(cursor.getColumnIndex(PRIX_DEPART_COL));

            produit = new Produit();
            produit.setId(id);
            produit.setNom(nom);
            produit.setMarqueId(marqueId);
            produit.setCategorieId(categorieId);
            produit.setPrixDepart(prixDepart);
            produit.setAnneeModel(anneeModel);

            Marque marque = getMarqueByID(marqueId);
            Categorie categorie = getCategorieByID(categorieId);

            produit.setMarque(marque);
            produit.setCategorie(categorie);

            cursor.close();
        }
        db.close();
        return produit;
    }

    public Marque getMarqueByID(int marqueID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM marque WHERE ID = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(marqueID)});

        Marque marque = null;

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(ID_COL));
            @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(NOM_COL));

            marque = new Marque();
            marque.setId(id);
            marque.setNom(nom);

            cursor.close();
        }

        db.close();
        return marque;
    }


    public Categorie getCategorieByID(int categorieID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM categorie WHERE ID = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(categorieID)});

        Categorie categorie = null;

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(ID_COL));
            @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(NOM_COL));

            categorie = new Categorie();
            categorie.setId(id);
            categorie.setNom(nom);

            cursor.close();
        }

        db.close();
        return categorie;
    }

    public Magasin getMagasinByID(int magasinID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM magasin WHERE ID = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(magasinID)});

        Magasin magasin = null;

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(ID_COL));
            @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(NOM_COL));
            @SuppressLint("Range") String telephone = cursor.getString(cursor.getColumnIndex(TELEPHONE_COL));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(EMAIL_COL));
            @SuppressLint("Range") String adresse = cursor.getString(cursor.getColumnIndex(ADRESSE_COL));
            @SuppressLint("Range") String ville = cursor.getString(cursor.getColumnIndex(VILLE_COL));
            @SuppressLint("Range") String etat = cursor.getString(cursor.getColumnIndex(ETAT_COL));
            @SuppressLint("Range") String codeZip = cursor.getString(cursor.getColumnIndex(CODE_ZIP_COL));

            magasin = new Magasin();
            magasin.setId(id);
            magasin.setNom(nom);
            magasin.setTelephone(telephone);
            magasin.setEmail(email);
            magasin.setAdresse(adresse);
            magasin.setVille(ville);
            magasin.setEtat(etat);
            magasin.setCodeZip(codeZip);

            cursor.close();
        }

        db.close();
        return magasin;
    }

    public void createTableMagasin(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + MAGASIN_TABLE_NAME + "("
                + ID_COL + " INTEGER PRIMARY KEY,"
                + NOM_COL + " VARCHAR(255),"
                + TELEPHONE_COL + " VARCHAR(20),"
                + EMAIL_COL + " VARCHAR(255),"
                + ADRESSE_COL + " VARCHAR(255),"
                + VILLE_COL + " VARCHAR(255),"
                + ETAT_COL + " VARCHAR(255),"
                + CODE_ZIP_COL + " VARCHAR(10))";
        db.execSQL(sqlQuery);
    }

    public void createTableMarque(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + MARQUE_TABLE_NAME + "("
                + ID_COL + " INTEGER PRIMARY KEY,"
                + NOM_COL + " VARCHAR(255))";
        db.execSQL(sqlQuery);
    }

    public void createTableCategorie(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + CATEGORIE_TABLE_NAME + "("
                + ID_COL + " INTEGER PRIMARY KEY,"
                + NOM_COL + " VARCHAR(255))";
        db.execSQL(sqlQuery);
    }

    public void createTableProduit(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + PRODUCT_TABLE_NAME + "("
                + ID_COL + " INTEGER PRIMARY KEY,"
                + NOM_COL + " VARCHAR(255),"
                + MARQUE_ID_COL + " INTEGER NOT NULL,"
                + CATEGORIE_ID_COL + " INTEGER NOT NULL,"
                + ANNEE_MODEL_COL + " INTEGER,"
                + PRIX_DEPART_COL + " DECIMAL(19, 2),"
                + "CONSTRAINT fk_produit_marque_id FOREIGN KEY (" + MARQUE_ID_COL + ") REFERENCES marque(ID) ON DELETE CASCADE,"
                + "CONSTRAINT fk_produit_marque_id FOREIGN KEY (" + CATEGORIE_ID_COL + ") REFERENCES categorie(ID) ON DELETE CASCADE)";
        db.execSQL(sqlQuery);
    }

    public void createTableStock(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + STOCK_TABLE_NAME + "("
                + MAGASIN_ID_COL + " INTEGER,"
                + PRODUIT_ID_COL + " INTEGER,"
                + QUANTITE_COL + " INTEGER,"
                + "PRIMARY KEY (" + MAGASIN_ID_COL + ", " + PRODUIT_ID_COL + "),"
                + "FOREIGN KEY (" + PRODUIT_ID_COL + ") REFERENCES produit(ID) ON DELETE CASCADE,"
                + "FOREIGN KEY (" + MAGASIN_ID_COL + ") REFERENCES magasin(ID))";
        db.execSQL(sqlQuery);
    }

    public void initMagasin(List<Magasin> magasinList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQuery = "INSERT INTO magasin(ID,NOM,TELEPHONE,EMAIL,ADRESSE,VILLE,ETAT,CODE_ZIP) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Use a prepared statement to safely insert data
        SQLiteStatement statement = db.compileStatement(sqlQuery);

        db.beginTransaction();

        try {
            for (Magasin magasin : magasinList) {
                statement.clearBindings();

                statement.bindLong(1, magasin.getId());
                statement.bindString(2, magasin.getNom());
                statement.bindString(3, magasin.getTelephone());
                statement.bindString(4, magasin.getEmail());
                statement.bindString(5, magasin.getAdresse());
                statement.bindString(6, magasin.getVille());
                statement.bindString(7, magasin.getEtat());
                statement.bindString(8, magasin.getCodeZip());

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            // Handle any exceptions here
        } finally {
            db.endTransaction();
        }
    }

    public void initMarque(List<Marque> marqueList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQuery = "INSERT INTO marque(ID, NOM) VALUES (?, ?)";

        SQLiteStatement statement = db.compileStatement(sqlQuery);

        db.beginTransaction();

        try {
            for (Marque marque : marqueList) {
                statement.clearBindings();

                statement.bindLong(1, marque.getId());
                statement.bindString(2, marque.getNom());

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            // Handle any exceptions here
        } finally {
            db.endTransaction();
        }
    }

    public void initCategorie(List<Categorie> categorieList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQuery = "INSERT INTO categorie(ID, NOM) VALUES (?, ?)";

        // Use a prepared statement to safely insert data
        SQLiteStatement statement = db.compileStatement(sqlQuery);

        db.beginTransaction();

        try {
            for (Categorie categorie : categorieList) {
                statement.clearBindings();

                statement.bindLong(1, categorie.getId());
                statement.bindString(2, categorie.getNom());

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            // Handle any exceptions here
        } finally {
            db.endTransaction();
        }
    }

    public void initProduit(List<Produit> produitList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQuery = "INSERT INTO produit(ID, NOM, MARQUE_ID, CATEGORIE_ID, ANNEE_MODEL, PRIX_DEPART) VALUES (?, ?, ?, ?, ?, ?)";

        // Use a prepared statement to safely insert data
        SQLiteStatement statement = db.compileStatement(sqlQuery);

        db.beginTransaction();

        try {
            for (Produit produit : produitList) {
                statement.clearBindings();

                statement.bindLong(1, produit.getId());
                statement.bindString(2, produit.getNom());
                statement.bindLong(3, produit.getMarqueId());
                statement.bindLong(4, produit.getCategorieId());
                statement.bindLong(5, produit.getAnneeModel());
                statement.bindDouble(6, produit.getPrixDepart());

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            // Handle any exceptions here
        } finally {
            db.endTransaction();
        }
    }

    public void initStock(List<Stock> stockList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQuery = "INSERT INTO stock(MAGASIN_ID, PRODUIT_ID, QUANTITE) VALUES (?, ?, ?)";

        // Use a prepared statement to safely insert data
        SQLiteStatement statement = db.compileStatement(sqlQuery);

        db.beginTransaction();

        try {
            for (Stock stock : stockList) {
                statement.clearBindings();

                statement.bindLong(1, stock.getId().getMagasinId());
                statement.bindLong(2, stock.getId().getProduitId());
                statement.bindLong(3, stock.getQuantite());

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            // Handle any exceptions here
        } finally {
            db.endTransaction();
        }
    }
}
