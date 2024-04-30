package edu.sdccd.cisc191.repositories;

import edu.sdccd.cisc191.database.Trigram;
import org.springframework.data.repository.CrudRepository;

public interface TrigramRepository extends CrudRepository<Trigram, String> {
}
