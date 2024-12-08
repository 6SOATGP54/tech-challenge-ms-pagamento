package com.tech_challenge.ms_pagamento;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tech_challenge.ms_pagamento.util.Assembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

public class AssemblerTest {

    @Mock
    private ModelMapper modelMapper;

    private Assembler assembler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assembler = new Assembler(modelMapper);
    }

    @Test
    void testToEntity() {
        // Arrange
        SomeDTO dto = new SomeDTO("Test Name", 30);
        SomeEntity expectedEntity = new SomeEntity("Test Name", 30);

        // Mock do modelMapper
        when(modelMapper.map(dto, SomeEntity.class)).thenReturn(expectedEntity);

        // Act
        SomeEntity result = Assembler.toEntity(dto, SomeEntity.class);

        // Assert
        assertNotNull(result);
        assertEquals(expectedEntity.getName(), result.getName());
        assertEquals(expectedEntity.getAge(), result.getAge());
    }

    @Test
    void testToModel() {
        // Arrange
        SomeEntity entity = new SomeEntity("Test Name", 30);
        SomeDTO expectedDTO = new SomeDTO("Test Name", 30);

        // Mock do modelMapper
        when(modelMapper.map(entity, SomeDTO.class)).thenReturn(expectedDTO);

        // Act
        SomeDTO result = Assembler.toModel(entity, SomeDTO.class);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.getName(), result.getName());
        assertEquals(expectedDTO.getAge(), result.getAge());
    }

    // Model classes for testing
    public static class SomeDTO {
        private String name;
        private int age;

        public SomeDTO(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // getters and setters...
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class SomeEntity {
        private String name;
        private int age;

        public SomeEntity(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // getters and setters...
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
