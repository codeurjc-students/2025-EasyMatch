package es.codeurjc.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.ClubDTO;
import es.codeurjc.dto.ClubMapper;
import es.codeurjc.model.Club;
import es.codeurjc.repository.ClubRepository;

@Service
public class ClubService {

    public ClubService(ClubRepository clubRepository, ClubMapper mapper) {
        this.clubRepository = clubRepository;
        this.mapper = mapper;
    }
    
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

    @Transactional(readOnly = true)
    public ClubDTO getClub(long id) {
		return mapper.toDTO(clubRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}

    @Transactional(readOnly = true)
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

    public void createClubImage(long id, InputStream inputStream, long size) { 
		Club club = clubRepository.findById(id).orElseThrow();
		club.setImage(BlobProxy.generateProxy(inputStream, size)); 
		clubRepository.save(club); 
	}

    public ClubDTO createClub(ClubDTO clubDTO) throws IOException {
        Club club = mapper.toDomain(clubDTO);
        club.setSports(List.of());
        club.setNumberOfCourts(List.of());
        setClubImage(club, "/images/default-club-image.jpg");
        this.save(club);
        return toDTO(club);
    }

    public ClubDTO replaceClub(long id, ClubDTO updatedClubDTO) {
        if (clubRepository.existsById(id)) {
			Club club = clubRepository.findById(id).orElseThrow();
            Club updatedClub = mapper.toDomain(updatedClubDTO);
            updatedClub.setId(id);
            updatedClub.setImage(club.getImage());
            updatedClub.setSports(club.getSports());
            updatedClub.setNumberOfCourts(club.getNumberOfCourts());
            clubRepository.save(updatedClub);
            return toDTO(updatedClub);
 		} else {
 			throw new NoSuchElementException("Club with id " + id + " does not exist.");
 		}
    }

     public void replaceClubImage(long id, InputStream inputStream, long size) {
		Club club = clubRepository.findById(id).orElseThrow();

		if(club.getImage() == null){
			throw new NoSuchElementException();
		}

		club.setImage(BlobProxy.generateProxy(inputStream, size));

		clubRepository.save(club);
	}

    private void setClubImage(Club club, String classpathResource) throws IOException {
         try {
            Resource image = new ClassPathResource(classpathResource);
		    club.setImage(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error at processing the image");
        }
	
	}

}
