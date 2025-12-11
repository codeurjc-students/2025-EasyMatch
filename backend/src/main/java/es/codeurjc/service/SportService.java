package es.codeurjc.service;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.SportDTO;
import es.codeurjc.dto.SportMapper;
import es.codeurjc.model.Sport;
import es.codeurjc.repository.ClubRepository;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.repository.SportRepository;

@Service
public class SportService {

    public SportService(SportRepository sportRepository, MatchRepository matchRepository,
            ClubRepository clubRepository, SportMapper mapper) {
        this.sportRepository = sportRepository;
        this.matchRepository = matchRepository;
        this.clubRepository = clubRepository;
        this.mapper = mapper;
    }

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private SportMapper mapper;

    

    public Optional<Sport> findById(long id) {
		return sportRepository.findById(id);
	}

    public List<Sport> findAll() {
        return sportRepository.findAll();
    }

    public boolean exist(long id) {
        return sportRepository.existsById(id);  
    }

    public Sport save(Sport sport) {
        return sportRepository.save(sport);
    }

    public void delete(Long id) {
        Optional<Sport> sportOptional = sportRepository.findById(id);
        if (sportOptional.isPresent()) {
            if (matchRepository.existsBySportId(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ese deporte no se puede borrar porque hay partidos que lo usan.");
            }
            if(clubRepository.existsBySportsId(id)){
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ese deporte no se puede borrar porque hay clubes que lo usan.");
            }
            sportRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Sport with id " + id + " does not exist.");
        }
    }


    public Collection<SportDTO> getSports() {
        return mapper.toDTOs(sportRepository.findAll());
    }

    public SportDTO getSport(long id) {
        Sport sport = sportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deporte no encontrado"));
        return mapper.toDTO(sport);
    }

    public SportDTO createSport(SportDTO sportDTO) {
        Sport sport = mapper.toDomain(sportDTO);
        this.save(sport);
        return mapper.toDTO(sport);
    }

    public SportDTO replaceSport(long id, SportDTO updatedSportDTO) {
        if (sportRepository.existsById(id)) {
            Sport updatedSport = mapper.toDomain(updatedSportDTO);
            updatedSport.setId(id);
            sportRepository.save(updatedSport);
            return mapper.toDTO(updatedSport);
 		} else {
 			throw new NoSuchElementException();
 		}
    }
}
