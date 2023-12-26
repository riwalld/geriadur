package com.example.geriadur.domain;

import com.example.geriadur.domain.consultation.Lexeme;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**The etymon define the proto-celtic root world and all his descandant
 *
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "etymon_name")
public class EtymonName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "etymon_id")
    private Long etymonId;

    @Column(name = "current_name", nullable = false)
    private String currentName;

    @Column(name = "etymon_name", nullable = false)
    private String etymonName;

    /** linked proto-celtic lexeme*/

    @ManyToMany(mappedBy = "etymonNames")
    private Set<Lexeme> lexemePc;

    @Column(name = "descr_fr")
    private String descrFr;

    @Column(name = "descr_eng")
    private String descrEng;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "sem_field_id")
    private SemanticField semanticField;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "lit_trans_id")
    private LiteralTranslation litTrans;

}