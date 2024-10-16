package io.openbas.rest.asset_group;

import static io.openbas.database.model.User.ROLE_USER;
import static io.openbas.helper.StreamHelper.iterableToSet;
import static io.openbas.utils.pagination.PaginationUtils.buildPaginationJPA;

import io.openbas.asset.AssetGroupService;
import io.openbas.database.model.AssetGroup;
import io.openbas.database.raw.RawPaginationAssetGroup;
import io.openbas.database.repository.AssetGroupRepository;
import io.openbas.database.repository.TagRepository;
import io.openbas.rest.asset_group.form.AssetGroupInput;
import io.openbas.rest.asset_group.form.UpdateAssetsOnAssetGroupInput;
import io.openbas.utils.pagination.SearchPaginationInput;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Secured(ROLE_USER)
public class AssetGroupApi {

  public static final String ASSET_GROUP_URI = "/api/asset_groups";

  private final AssetGroupService assetGroupService;
  private final AssetGroupRepository assetGroupRepository;
  private final TagRepository tagRepository;

  @PostMapping(ASSET_GROUP_URI)
  @PreAuthorize("isPlanner()")
  @Transactional(rollbackOn = Exception.class)
  public AssetGroup createAssetGroup(@Valid @RequestBody final AssetGroupInput input) {
    AssetGroup assetGroup = new AssetGroup();
    assetGroup.setUpdateAttributes(input);
    assetGroup.setTags(iterableToSet(this.tagRepository.findAllById(input.getTagIds())));
    return this.assetGroupService.createAssetGroup(assetGroup);
  }

  @GetMapping(ASSET_GROUP_URI)
  @PreAuthorize("isObserver()")
  public List<AssetGroup> assetGroups() {
    return this.assetGroupService.assetGroups();
  }

  @PostMapping(ASSET_GROUP_URI + "/search")
  @PreAuthorize("isObserver()")
  public Page<RawPaginationAssetGroup> assetGroups(
      @RequestBody @Valid SearchPaginationInput searchPaginationInput) {
    return buildPaginationJPA(
            this.assetGroupRepository::findAll, searchPaginationInput, AssetGroup.class)
        .map(RawPaginationAssetGroup::new);
  }

  @GetMapping(ASSET_GROUP_URI + "/{assetGroupId}")
  @PreAuthorize("isObserver()")
  public AssetGroup assetGroup(@PathVariable @NotBlank final String assetGroupId) {
    return this.assetGroupService.assetGroup(assetGroupId);
  }

  @PutMapping(ASSET_GROUP_URI + "/{assetGroupId}")
  @PreAuthorize("isPlanner()")
  @Transactional(rollbackOn = Exception.class)
  public AssetGroup updateAssetGroup(
      @PathVariable @NotBlank final String assetGroupId,
      @Valid @RequestBody final AssetGroupInput input) {
    AssetGroup assetGroup = this.assetGroupService.assetGroup(assetGroupId);
    assetGroup.setUpdateAttributes(input);
    assetGroup.setTags(iterableToSet(this.tagRepository.findAllById(input.getTagIds())));
    return this.assetGroupService.updateAssetGroup(assetGroup);
  }

  @PutMapping(ASSET_GROUP_URI + "/{assetGroupId}/assets")
  @PreAuthorize("isPlanner()")
  @Transactional(rollbackOn = Exception.class)
  public AssetGroup updateAssetsOnAssetGroup(
      @PathVariable @NotBlank final String assetGroupId,
      @Valid @RequestBody final UpdateAssetsOnAssetGroupInput input) {
    AssetGroup assetGroup = this.assetGroupService.assetGroup(assetGroupId);
    return this.assetGroupService.updateAssetsOnAssetGroup(assetGroup, input.getAssetIds());
  }

  @DeleteMapping(ASSET_GROUP_URI + "/{assetGroupId}")
  @PreAuthorize("isPlanner()")
  @Transactional(rollbackOn = Exception.class)
  public void deleteAssetGroup(@PathVariable @NotBlank final String assetGroupId) {
    this.assetGroupService.deleteAssetGroup(assetGroupId);
  }
}
