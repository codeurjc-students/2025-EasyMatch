package es.codeurjc.service;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.dto.ClubDTO;
import es.codeurjc.dto.ClubMapper;
import es.codeurjc.model.Club;
import es.codeurjc.repository.ClubRepository;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubMapper mapper;

    private ClubDTO toDTO (Club club) {
        return mapper.toDTO(club);
    }

    public Optional<Club> findById(long id) {
        return clubRepository.findById(id);
    }

    public List<Club> findAll() {
        return clubRepository.findAll();
    }

    public Page<Club> findAll(Pageable pageable) {
        return clubRepository.findAll(pageable);
    }

    public boolean exist(long id) {
        return clubRepository.existsById(id);
    }

    public Club save(Club club) {
        return clubRepository.save(club);
    }
    public void delete(long id) {
        Optional<Club> clubOptional = clubRepository.findById(id);
        if (clubOptional.isPresent()) {
            clubRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Club with id " + id + " does not exist.");
        }
    }

    public ClubDTO getClub(long id) {
		return mapper.toDTO(clubRepository.findById(id).orElseThrow());
	}

    public Page<ClubDTO> getClubs(Pageable pageable) {
        return findAll(pageable).map(this::toDTO);
    }

    public Page<Club> getFilteredClubs(Pageable pageable, String search, String sport, String city) {
        return clubRepository.findFilteredClubs(pageable,
                search != null ? "%" + search.toLowerCase() + "%" : null,
                sport != null ? sport.toLowerCase() : null,
                city != null ? city.toLowerCase() : null);
    }

    public Resource getClubImage(long id) throws SQLException{
		Club club = clubRepository.findById(id).orElseThrow();

		if (club.getImage() != null) {
			return new InputStreamResource(club.getImage().getBinaryStream());
		} else {
			throw new NoSuchElementException();
		}
    }

    public void createUserImage(long id, InputStream inputStream, long size) { 
		Club club = clubRepository.findById(id).orElseThrow();
		club.setImage(BlobProxy.generateProxy(inputStream, size)); 
		clubRepository.save(club); 
	}

}
