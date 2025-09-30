package es.codeurjc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.model.Club;
import es.codeurjc.repository.ClubRepository;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;

    public Optional<Club> findById(long id) {
        return clubRepository.findById(id);
    }

    public List<Club> findAll() {
        return clubRepository.findAll();
    }
    public boolean exist(long id) {
        return clubRepository.existsById(id);
    }

    public Club save(Club club) {
        return clubRepository.save(club);
    }
    public void deleteById(long id) {
        Optional<Club> clubOptional = clubRepository.findById(id);
        if (clubOptional.isPresent()) {
            clubRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Club with id " + id + " does not exist.");
        }
    }

}
