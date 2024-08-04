package com.francode.college_political_parties.Utils;

import com.francode.college_political_parties.Model.TipoDocumento;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TypeDocService {

    @GET("/tipodocumento/listar")
    Call<List<TipoDocumento>> listAll();

    @GET("/tipodocumento/find/{id}")
    Call<TipoDocumento> findById(@Path("id") int id);

    @GET("/tipodocumento/search")
    Call<List<TipoDocumento>> search(@Query("nombre") String nombre, @Query("nombre_corto") String nombre_corto);

    @POST("/tipodocumento/save")
    Call<TipoDocumento> save(@Body TipoDocumento tipoDocumento);

    @PUT("/tipodocumento/update")
    Call<TipoDocumento> update(@Body TipoDocumento tipoDocumento);

    @GET("/tipodocumento/disable/{id}")
    Call<TipoDocumento> disable(@Path("id") int id);
}
