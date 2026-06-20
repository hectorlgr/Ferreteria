package com.ferreteria.catalogo_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteria.catalogo_service.Dto.ProductoRequestDto;
import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.service.ProductoService;

@ExtendWith(MockitoExtension.class)
public class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Producto productoMock;
    private ProductoRequestDto dtoMock;

    @BeforeEach
    void setUp() {
        ProductoController controller = new ProductoController(productoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Martillo de Uña");
        productoMock.setDescripcion("Mango de fibra");
        productoMock.setMarca("Stanley");
        productoMock.setPrecio(14990);
        productoMock.setHabilitado(true);

        dtoMock = new ProductoRequestDto();
        dtoMock.setNombre("Martillo de Uña");
        dtoMock.setDescripcion("Mango de fibra");
        dtoMock.setMarca("Stanley");
        dtoMock.setPrecio(14990); 
    }

    @Test
    public void testGuardarProducto() throws Exception {
        // GIVEN
        when(productoService.guardarProducto(any(Producto.class))).thenReturn(productoMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Martillo de Uña"))
                .andExpect(jsonPath("$.precio").value(14990));
                
        verify(productoService, times(1)).guardarProducto(any(Producto.class));
    }

    @Test
    public void testObtenerTodos() throws Exception {
        // GIVEN
        when(productoService.obtenerTodos()).thenReturn(Arrays.asList(productoMock));

        // WHEN & THEN
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoList[0].nombre").value("Martillo de Uña"))
                .andExpect(jsonPath("$._embedded.productoList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
                
        verify(productoService, times(1)).obtenerTodos();
    }
    
    @Test
    public void testObtenerPorId() throws Exception {
        // GIVEN
        when(productoService.obtenerPorId(1L)).thenReturn(productoMock);

        // WHEN & THEN
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.nombre").value("Martillo de Uña"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-los-productos.href").exists());
                
        verify(productoService, times(1)).obtenerPorId(1L);
    }
}