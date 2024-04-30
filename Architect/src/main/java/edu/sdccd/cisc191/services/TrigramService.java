package edu.sdccd.cisc191.services;

import edu.sdccd.cisc191.database.Trigram;
import edu.sdccd.cisc191.repositories.TrigramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrigramService {
    @Autowired
    TrigramRepository trigramRepository;
    public Trigram save(Trigram trigram){
        return trigramRepository.save(trigram);
    }
}
