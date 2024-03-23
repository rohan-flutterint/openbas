package io.openbas.rest.attack_pattern;

import io.openbas.database.model.AttackPattern;
import io.openbas.database.repository.AttackPatternRepository;
import io.openbas.database.repository.KillChainPhaseRepository;
import io.openbas.rest.attack_pattern.form.AttackPatternCreateInput;
import io.openbas.rest.helper.RestBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static io.openbas.database.model.User.ROLE_ADMIN;
import static io.openbas.database.model.User.ROLE_USER;
import static io.openbas.helper.DatabaseHelper.updateRelation;
import static io.openbas.helper.StreamHelper.fromIterable;

@RestController
@Secured(ROLE_USER)
public class AttackPatternApi extends RestBehavior {

    private AttackPatternRepository attackPatternRepository;
    private KillChainPhaseRepository killChainPhaseRepository;

    @Autowired
    public void setAttackPatternRepository(AttackPatternRepository attackPatternRepository) {
        this.attackPatternRepository = attackPatternRepository;
    }

    @Autowired
    public void setKillChainPhaseRepository(KillChainPhaseRepository killChainPhaseRepository) {
        this.killChainPhaseRepository = killChainPhaseRepository;
    }

    @GetMapping("/api/attack_patterns")
    public Iterable<AttackPattern> attackPatterns() {
        return attackPatternRepository.findAll();
    }

    @GetMapping("/api/attack_patterns/{attackPatternId}")
    public AttackPattern attackPattern(@PathVariable String attackPatternId) {
        return attackPatternRepository.findById(attackPatternId).orElseThrow();
    }

    @Secured(ROLE_ADMIN)
    @PostMapping("/api/attack_patterns")
    public AttackPattern createAttackPattern(@Valid @RequestBody AttackPatternCreateInput input) {
        AttackPattern attackPattern = new AttackPattern();
        attackPattern.setUpdateAttributes(input);
        attackPattern.setKillChainPhases(fromIterable(killChainPhaseRepository.findAllById(input.getKillChainPhasesIds())));
        attackPattern.setParent(updateRelation(input.getParentId(), attackPattern.getParent(), attackPatternRepository));
        return attackPatternRepository.save(attackPattern);
    }

    @Secured(ROLE_ADMIN)
    @DeleteMapping("/api/attack_patterns/{attackPatternId}")
    public void deleteAttackPattern(@PathVariable String attackPatternId) {
        attackPatternRepository.deleteById(attackPatternId);
    }
}