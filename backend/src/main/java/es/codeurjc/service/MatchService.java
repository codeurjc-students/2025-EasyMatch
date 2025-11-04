package es.codeurjc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.model.Match;
import es.codeurjc.repository.MatchRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatchService {

    public MatchService(MatchRepository matchRepository, MatchMapper mapper) {
        this.matchRepository = matchRepository;
        this.mapper = mapper;
    }

    @Autowired
	private MatchRepository matchRepository;

    @Autowired
    private MatchMapper mapper;

    private MatchDTO toDTO (Match Match) {
        return mapper.toDTO(Match);
    }

    public Optional<Match> findById(long id) {
		return matchRepository.findById(id);
	}

    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    public Page<Match> findAll(Pageable pageable) {
		return matchRepository.findAll(pageable);
	}

    @Transactional(readOnly = true)
    public MatchDTO getMatch(long id) {
        return toDTO(findById(id).orElseThrow());
    }
    
    @Transactional(readOnly = true)
    public Page<MatchDTO> getMatches(Pageable pageable) {
        return findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<MatchDTO> getFilteredMatches( Pageable pageable, String search, String sport, Boolean includeFriendlies, String timeRange) {
        if (includeFriendlies == null) {
            includeFriendlies = true;
        }
        return matchRepository.findFilteredMatches(pageable,
            search != null ? "%" + search.toLowerCase() + "%" : null, 
            sport != null ? sport.toLowerCase() : null, 
            includeFriendlies, 
            timeRange != null ? timeRange.toLowerCase() : null)
            .map(this::toDTO);
    }

    public boolean exist(long id) {
        return matchRepository.existsById(id);  
    }
    public Match save(Match match) {
        return matchRepository.save(match);
    }

    public void delete(Long id) {
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isPresent()) {
            matchRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Match with id " + id + " does not exist.");
        }
    }

    
}
