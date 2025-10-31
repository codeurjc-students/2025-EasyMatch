package es.codeurjc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.model.Sport;
import es.codeurjc.repository.SportRepository;

@Service
public class SportService {

    @Autowired
    private SportRepository sportRepository;

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
            sportRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Sport with id " + id + " does not exist.");
        }
    }
}
