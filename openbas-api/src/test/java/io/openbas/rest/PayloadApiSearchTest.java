package io.openbas.rest;

import static io.openbas.database.model.Endpoint.PLATFORM_ARCH.arm64;
import static io.openbas.database.model.Endpoint.PLATFORM_TYPE.Linux;
import static io.openbas.database.model.Filters.FilterOperator.contains;
import static io.openbas.database.model.Payload.PAYLOAD_SOURCE.MANUAL;
import static io.openbas.rest.payload.PayloadApi.PAYLOAD_URI;
import static io.openbas.utils.JsonUtils.asJsonString;
import static io.openbas.utils.fixtures.PayloadFixture.*;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.openbas.IntegrationTest;
import io.openbas.database.model.Payload;
import io.openbas.database.repository.PayloadRepository;
import io.openbas.utils.fixtures.PaginationFixture;
import io.openbas.utils.mockUser.WithMockAdminUser;
import io.openbas.utils.pagination.SearchPaginationInput;
import io.openbas.utils.pagination.SortField;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@TestInstance(PER_CLASS)
public class PayloadApiSearchTest extends IntegrationTest {

  @Autowired private MockMvc mvc;

  @Autowired private PayloadRepository payloadRepository;

  private static final List<String> PAYLOAD_COMMAND_IDS = new ArrayList<>();

  @BeforeAll
  void beforeAll() {
    Payload command = createDefaultCommand();
    Payload commandSaved = this.payloadRepository.save(command);
    PAYLOAD_COMMAND_IDS.add(commandSaved.getId());

    Payload dnsResolution = createDefaultDnsResolution();
    Payload dnsResolutionSaved = this.payloadRepository.save(dnsResolution);
    PAYLOAD_COMMAND_IDS.add(dnsResolutionSaved.getId());

    Payload executable = createDefaultExecutable();
    Payload executableSaved = this.payloadRepository.save(executable);
    PAYLOAD_COMMAND_IDS.add(executableSaved.getId());
  }

  @AfterAll
  void afterAll() {
    this.payloadRepository.deleteAllById(PAYLOAD_COMMAND_IDS);
  }

  @Nested
  @WithMockAdminUser
  @DisplayName("Retrieving payloads")
  class RetrievingPayloads {
    // -- PREPARE --

    @Nested
    @DisplayName("Searching page of payloads")
    class SearchingPageOfPayloads {

      @Test
      @DisplayName("Retrieving first page of payloads by textsearch")
      void given_working_search_input_should_return_a_page_of_payloads() throws Exception {
        SearchPaginationInput searchPaginationInput =
            PaginationFixture.getDefault().textSearch("command").build();

        mvc.perform(
                post(PAYLOAD_URI + "/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(searchPaginationInput)))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.numberOfElements").value(1));
      }

      @Test
      @DisplayName("Not retrieving first page of payloads by textsearch")
      void given_not_working_search_input_should_return_a_page_of_payloads() throws Exception {
        SearchPaginationInput searchPaginationInput =
            PaginationFixture.getDefault().textSearch("wrong").build();

        mvc.perform(
                post(PAYLOAD_URI + "/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(searchPaginationInput)))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.numberOfElements").value(0));
      }
    }

    @Nested
    @DisplayName("Sorting page of payloads")
    class SortingPageOfPayloads {

      @Test
      @DisplayName("Sorting page of payloads by name")
      void given_sorting_input_by_name_should_return_a_page_of_payloads_sort_by_name()
          throws Exception {
        SearchPaginationInput searchPaginationInput =
            PaginationFixture.getDefault()
                .sorts(List.of(SortField.builder().property("payload_name").build()))
                .build();

        mvc.perform(
                post(PAYLOAD_URI + "/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(searchPaginationInput)))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.content.[0].payload_name").value("command payload"))
            .andExpect(jsonPath("$.content.[1].payload_name").value("dns resolution payload"));
      }

      @Test
      @DisplayName("Sorting page of payloads by platforms")
      void given_sorting_input_by_updated_at_should_return_a_page_of_payloads_sort_by_updated_at()
          throws Exception {
        SearchPaginationInput searchPaginationInput =
            PaginationFixture.getDefault()
                .sorts(
                    List.of(
                        SortField.builder()
                            .property("payload_updated_at")
                            .direction("desc")
                            .build()))
                .build();

        mvc.perform(
                post(PAYLOAD_URI + "/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(searchPaginationInput)))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.content.[0].payload_name").value("executable payload"))
            .andExpect(jsonPath("$.content.[1].payload_name").value("dns resolution payload"))
            .andExpect(jsonPath("$.content.[2].payload_name").value("command payload"));
      }
    }

    @Nested
    @DisplayName("Filtering page of payloads")
    class FilteringPageOfPayloads {

      @Test
      @DisplayName("Filtering page of payloads by name")
      void given_filter_input_by_name_should_return_a_page_of_payloads_filter_by_name()
          throws Exception {
        SearchPaginationInput searchPaginationInput =
            PaginationFixture.simpleFilter("payload_name", "command", contains);

        mvc.perform(
                post(PAYLOAD_URI + "/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(searchPaginationInput)))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.numberOfElements").value(1));
      }

      @Test
      @DisplayName("Filtering page of payloads by platforms")
      void given_filter_input_by_platforms_should_return_a_page_of_payloads_filter_by_platforms()
          throws Exception {
        SearchPaginationInput searchPaginationInput =
            PaginationFixture.simpleFilter("payload_platforms", valueOf(Linux), contains);

        mvc.perform(
                post(PAYLOAD_URI + "/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(searchPaginationInput)))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.numberOfElements").value(1));
      }

      @Test
      @DisplayName("Filtering page of payloads by source")
      void given_filter_input_by_source_should_return_a_page_of_payloads_filter_by_source()
          throws Exception {
        SearchPaginationInput searchPaginationInput =
            PaginationFixture.simpleFilter("payload_source", valueOf(MANUAL), contains);

        mvc.perform(
                post(PAYLOAD_URI + "/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(searchPaginationInput)))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.numberOfElements").value(3));
      }

      @Test
      @DisplayName("Filtering page of payloads by architecture")
      void
          given_filter_input_by_arch_should_return_a_page_of_executable_payloads_filtered_by_architecture()
              throws Exception {
        SearchPaginationInput searchPaginationInput =
            PaginationFixture.simpleFilter("executable_arch", valueOf(arm64), contains);

        mvc.perform(
                post(PAYLOAD_URI + "/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(searchPaginationInput)))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.numberOfElements").value(1));
      }
    }
  }
}
