package io.openbas.rest;

import static io.openbas.utils.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.openbas.database.model.ImportMapper;
import io.openbas.database.repository.ImportMapperRepository;
import io.openbas.rest.mapper.MapperApi;
import io.openbas.rest.mapper.form.ImportMapperAddInput;
import io.openbas.rest.mapper.form.ImportMapperUpdateInput;
import io.openbas.rest.scenario.form.InjectsImportTestInput;
import io.openbas.rest.scenario.response.ImportTestSummary;
import io.openbas.service.InjectService;
import io.openbas.service.MapperService;
import io.openbas.utils.fixtures.PaginationFixture;
import io.openbas.utils.mockMapper.MockMapperUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MapperApiTest {

  private MockMvc mvc;

  @Mock private ImportMapperRepository importMapperRepository;

  @Mock private MapperService mapperService;
  @Mock private InjectService injectService;

  private MapperApi mapperApi;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void before() throws IllegalAccessException, NoSuchFieldException {
    // Injecting mocks into the controller
    mapperApi = new MapperApi(importMapperRepository, mapperService, injectService);

    Field sessionContextField = MapperApi.class.getSuperclass().getDeclaredField("mapper");
    sessionContextField.setAccessible(true);
    sessionContextField.set(mapperApi, objectMapper);

    mvc = MockMvcBuilders.standaloneSetup(mapperApi).build();
  }

  // -- SCENARIOS --

  @DisplayName("Test search of mappers")
  @Test
  void searchMappers() throws Exception {
    // -- PREPARE --
    List<ImportMapper> importMappers = List.of(MockMapperUtils.createImportMapper());
    Pageable pageable = PageRequest.of(0, 10);
    PageImpl page = new PageImpl<>(importMappers, pageable, importMappers.size());
    when(importMapperRepository.findAll(any(), any())).thenReturn(page);
    // -- EXECUTE --
    String response =
        this.mvc
            .perform(
                MockMvcRequestBuilders.post("/api/mappers/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(PaginationFixture.getDefault().textSearch("").build())))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // -- ASSERT --
    assertNotNull(response);
    assertEquals(
        JsonPath.read(response, "$.content[0].import_mapper_id"), importMappers.get(0).getId());
  }

  @DisplayName("Test search of a specific mapper")
  @Test
  void searchSpecificMapper() throws Exception {
    // -- PREPARE --
    ImportMapper importMapper = MockMapperUtils.createImportMapper();
    when(importMapperRepository.findById(any())).thenReturn(Optional.of(importMapper));
    // -- EXECUTE --
    String response =
        this.mvc
            .perform(
                MockMvcRequestBuilders.get("/api/mappers/" + importMapper.getId())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // -- ASSERT --
    assertNotNull(response);
    assertEquals(JsonPath.read(response, "$.import_mapper_id"), importMapper.getId());
  }

  @DisplayName("Test create a mapper")
  @Test
  void createMapper() throws Exception {
    // -- PREPARE --
    ImportMapper importMapper = MockMapperUtils.createImportMapper();
    ImportMapperAddInput importMapperInput = new ImportMapperAddInput();
    importMapperInput.setName("Test");
    importMapperInput.setInjectTypeColumn("B");
    when(mapperService.createAndSaveImportMapper(any())).thenReturn(importMapper);
    // -- EXECUTE --
    String response =
        this.mvc
            .perform(
                MockMvcRequestBuilders.post("/api/mappers/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(importMapperInput)))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // -- ASSERT --
    assertNotNull(response);
    assertEquals(JsonPath.read(response, "$.import_mapper_id"), importMapper.getId());
  }

  @DisplayName("Test duplicate a mapper")
  @Test
  void duplicateMapper() throws Exception {
    // -- PREPARE --
    ImportMapper importMapper = MockMapperUtils.createImportMapper();
    ImportMapper importMapperDuplicated = MockMapperUtils.createImportMapper();
    when(mapperService.getDuplicateImportMapper(any())).thenReturn(importMapperDuplicated);

    // -- EXECUTE --
    String response =
        this.mvc
            .perform(
                MockMvcRequestBuilders.post("/api/mappers/" + importMapper.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(importMapper)))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // -- ASSERT --
    assertNotNull(response);
    assertEquals(JsonPath.read(response, "$.import_mapper_id"), importMapperDuplicated.getId());
  }

  @DisplayName("Test delete a specific mapper")
  @Test
  void deleteSpecificMapper() throws Exception {
    // -- PREPARE --
    ImportMapper importMapper = MockMapperUtils.createImportMapper();
    // -- EXECUTE --
    this.mvc
        .perform(
            MockMvcRequestBuilders.delete("/api/mappers/" + importMapper.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(PaginationFixture.getDefault().textSearch("").build())))
        .andExpect(status().is2xxSuccessful());

    verify(importMapperRepository, times(1)).deleteById(any());
  }

  @DisplayName("Test update a specific mapper by using new rule attributes and new inject importer")
  @Test
  void updateSpecificMapper() throws Exception {
    // -- PREPARE --
    ImportMapper importMapper = MockMapperUtils.createImportMapper();
    ImportMapperUpdateInput importMapperInput = new ImportMapperUpdateInput();
    importMapperInput.setName("New name");
    importMapperInput.setInjectTypeColumn("B");
    when(mapperService.updateImportMapper(any(), any())).thenReturn(importMapper);
    // -- EXECUTE --
    String response =
        this.mvc
            .perform(
                MockMvcRequestBuilders.put("/api/mappers/" + importMapper.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(importMapperInput)))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // -- ASSERT --
    assertNotNull(response);
    assertEquals(JsonPath.read(response, "$.import_mapper_id"), importMapper.getId());
  }

  @DisplayName("Test store xls")
  @Test
  void testStoreXls() throws Exception {
    // -- PREPARE --
    // Getting a test file
    File testFile = ResourceUtils.getFile("classpath:xls-test-files/test_file_1.xlsx");

    InputStream in = new FileInputStream(testFile);
    MockMultipartFile xlsFile =
        new MockMultipartFile("file", "my-awesome-file.xls", "application/xlsx", in.readAllBytes());

    // -- EXECUTE --
    String response =
        this.mvc
            .perform(MockMvcRequestBuilders.multipart("/api/mappers/store").file(xlsFile))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // -- ASSERT --
    assertNotNull(response);
  }

  @DisplayName("Test testing an import xls")
  @Test
  void testTestingXls() throws Exception {
    // -- PREPARE --
    InjectsImportTestInput injectsImportInput = new InjectsImportTestInput();
    injectsImportInput.setImportMapper(new ImportMapperAddInput());
    injectsImportInput.setName("TEST");
    injectsImportInput.setTimezoneOffset(120);
    ImportMapper importMapper = MockMapperUtils.createImportMapper();

    injectsImportInput.getImportMapper().setName("TEST");

    when(injectService.importInjectIntoScenarioFromXLS(
            any(), any(), any(), any(), anyInt(), anyBoolean()))
        .thenReturn(new ImportTestSummary());
    when(mapperService.createImportMapper(any())).thenReturn(importMapper);

    // -- EXECUTE --
    String response =
        this.mvc
            .perform(
                MockMvcRequestBuilders.post(
                        "/api/mappers/store/{importId}", UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(injectsImportInput)))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // -- ASSERT --
    assertNotNull(response);
  }
}
