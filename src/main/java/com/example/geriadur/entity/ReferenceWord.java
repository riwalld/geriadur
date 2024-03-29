package com.example.geriadur.entity;

/**
 * The referenceWord Entity registered french or english word
 * used as literal translation of the wordStems entities
 * This entity is not used for the moment, the translation is directly
 * registered as an attribute (column) of the wordStem entity
 */

/*
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "refWord")
public class ReferenceWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="refword_id")
    private Long id;

    @Column(name="ref_word_name")
    String refWordName;

    @Column(name="ref_word_language")
    LanguageEnum refWordNameLanguage;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST,CascadeType.MERGE}
    )
    @JoinTable(
            name="referenceWord_wordStem",
            joinColumns = @JoinColumn(name = "refWord_id"),
            inverseJoinColumns = @JoinColumn(name = "wordStem_id")
    )
    private Set<WordStem> wordStems = new HashSet<>();

}
*/

