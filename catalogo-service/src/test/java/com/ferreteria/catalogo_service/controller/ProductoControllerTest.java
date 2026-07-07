package com.ferreteria.catalogo_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ferreteria.catalogo_service.assembler.ProductoModelAssembler;
import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.service.ProductoService;

@ExtendWith(MockitoExtension.class)
public class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    private Producto productoMock;

    @BeforeEach
    void setUp() {
        ProductoModelAssembler assembler = new ProductoModelAssembler();

        ProductoController controller = new ProductoController(productoService, assembler);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Martillo de Uña");
        productoMock.setDescripcion("Mango de fibra");
        productoMock.setMarca("Stanley");
        productoMock.setPrecio(14990);
        productoMock.setHabilitado(true);
    }

    @Test
    public void testGuardarProducto() throws Exception {
        // GIVEN
        when(productoService.guardarProducto(any(Producto.class))).thenReturn(productoMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"nombre\":\"Martillo de Uña\",\"descripcion\":\"Mango de fibra\",\"marca\":\"Stanley\",\"precio\":14990}"))
                .andExpect(status().isCreated())
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Martillo de Uña"));

        verify(productoService, times(1)).obtenerTodos();
    }

    @Test
    public void testObtenerPorId() throws Exception {
        // GIVEN
        when(productoService.obtenerPorId(1L)).thenReturn(productoMock);

        // WHEN & THEN
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Martillo de Uña"));

        verify(productoService, times(1)).obtenerPorId(1L);
    }
}