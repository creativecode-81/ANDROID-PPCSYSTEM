package com.francode.college_political_parties.Utils;

import com.francode.college_political_parties.Model.Alumno;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StudentService {

    @GET("/alumno/listar")
    Call<List<Alumno>> listAll();

    @GET("/alumno/find/{id}")
    Call<Alumno> findById(@Path("id") int id);

    @GET("/alumno/search")
    Call<List<Alumno>> search(@Query("nombres") String nombres, @Query("apellido_paterno") String apellido_paterno, @Query("apellido_materno") String apellido_materno, @Query("nro_doc") String nro_doc, @Query("telefono") String telefono);

    @POST("/alumno/save")
    Call<Alumno> save(@Body Alumno alumno);

    @PUT("/alumno/update")
    Call<Alumno> update(@Body Alumno alumno);

    @GET("/alumno/disable/{id}")
    Call<Alumno> disable(@Path("id") int id);
    
}
