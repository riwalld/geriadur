package com.example.geriadur.service.consultation;

import com.example.geriadur.constants.GenderEnum;
import com.example.geriadur.constants.LanguageEnum;
import com.example.geriadur.constants.WordClassEnum;
import com.example.geriadur.dto.*;
import com.example.geriadur.entity.LiteralTranslation;
import com.example.geriadur.entity.ProperNoun;
import com.example.geriadur.entity.SemanticField;
import com.example.geriadur.entity.consultation.Source;
import com.example.geriadur.entity.consultation.WordStem;
import com.example.geriadur.entity.consultation.Quote;
import com.example.geriadur.repositories.*;
import com.example.geriadur.service.consultation.api.IWordStemService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
/**
 ** WordStemServiceImpl implement the interface WordStemService which allow CRUD
 ** fonctions on the object WordStem
 **/
public class WordStemService implements IWordStemService {

    @Autowired
    private WordStemRepository wordStemRepository;
    @Autowired
    private ProperNounRepository properNounRepository;
    @Autowired
    private QuoteRepository quoteRepository;
    @Autowired
    private SemanticFieldRepository semanticFieldRepository;
    @Autowired
    private SourceRepository sourceRepository;

    public List<ProperNounsDTO> getProperNouns() {
        List<ProperNoun> properNouns = properNounRepository.findAll();
        List<ProperNounsDTO> properNousDTOList = new ArrayList<>();

        for (int i = 0; i < properNouns.size(); ++i) {
            ProperNounsDTO properNousDTO = new ProperNounsDTO();
            properNousDTO.setCurrentName(properNouns.get(i).getCurrentName());
            properNousDTO.setWordTheme(properNouns.get(i).getWordTheme());
            properNousDTO.setCountry(properNouns.get(i).getCountry());
            properNousDTO.setPeriod(properNouns.get(i).getPeriod());
            properNousDTO.setPlace(properNouns.get(i).getPlace());
            properNousDTO.setYear(properNouns.get(i).getYear());
            properNousDTO.setShortDescrFr(properNouns.get(i).getShortDescrFr());
            properNousDTOList.add(properNousDTO);
        }

        return properNousDTOList;
    }

    public void addProperNoun(ProperNounsDTO properNousDTO) {
        ProperNoun properNoun = dtoProperNounToEntityProperNoun(properNousDTO);
        properNounRepository.save(properNoun);
        log.info("The proper noun stem: \"" + properNoun.getCurrentName() + "\" has been added.");
    }
    /*
     * public void saveAllProperNouns(List<CreateProperNoun> properNounsInit) {
     * List<ProperNoun> properNouns = new ArrayList<>();
     * for (CreateProperNoun createEtymo : properNounsInit) {
     * properNouns.add(dtoEtymonToEntityEtymon(createEtymo));
     * log.info("The proper noun stem: \"" + createEtymo.getEtymoName() +
     * "\" has been added.");
     * }
     * properNounRepository.saveAll(properNouns);
     * }
     */

    private ProperNoun dtoProperNounToEntityProperNoun(ProperNounsDTO properNounDTO) {
        LiteralTranslation literalTranslation = new LiteralTranslation();
        literalTranslation.setLitTransFr(properNounDTO.getLitTransFr());
        literalTranslation.setLitTransEng(properNounDTO.getLitTransEng());
        literalTranslation.setLitTransType(properNounDTO.getLitTransType());
        ProperNoun properNoun = new ProperNoun();
        literalTranslation.setEtymonName(properNoun);
        properNoun.setEtymoName(properNounDTO.getEtymoName());
        properNoun.setCurrentName(properNounDTO.getCurrentName());
        properNoun.setWordTheme(properNounDTO.getWordTheme());
        properNoun.setLitTrans(literalTranslation);
        properNoun.setDescrFr(properNounDTO.getDescrFr());
        properNoun.setDescrEng(properNounDTO.getDescrEng());
        Map<Integer, WordStem> wordStems = new HashMap<>();
        for (int i = 0; i < properNounDTO.getWordStemsPC().size(); i++) {
            wordStems.put(i, wordStemRepository.findByWordStemName(properNounDTO.getWordStemsPC().get(i)).get());
        }
        properNoun.setWordStemPc(wordStems);
        return properNoun;
    }

    public void setWordStemEtymonLink(String properNounStr, List<String> wordStemsString) {
        ProperNoun properNoun = properNounRepository.findProperNounByCurrentName(properNounStr).get();
        Map<Integer, WordStem> wordStems = new HashMap<>();
        for (int i = 0; i < wordStemsString.size(); ++i) {
            Optional<WordStem> wordStem = wordStemRepository.findByWordStemName(wordStemsString.get(i));
            if (wordStem.isPresent()) {
                wordStems.put(i, wordStem.get());
            } else
                throw new IllegalArgumentException(
                        "the wordStem: \"" + wordStemsString.get(i) + "\" doesn't exist in DB.");
        }
        log.info("Word Stems has been linked to the proper noun: \"" + properNoun.getCurrentName());
        properNoun.setWordStemPc(wordStems);
        properNounRepository.save(properNoun);
    }

    /**
     * addAWordStem(...) save a new wordstem from the dto CreateWordStem
     * and setting the links with the quotes, sources and parents
     */
    public void addAWordStem(CreateWordStem createWordStem) {
        WordStem wordStem = dtoWordstemToEntityWordstem(createWordStem);
        wordStemRepository.save(wordStem);
        log.info("The new word stem: \"" + wordStem.getWordStemName() + "\" has been added.");
    }

    /**
     * saveAllWordStems(...) save a list of wordstem in one DB request,
     * this useful when reseting the database with json data
     */
    public void saveAllWordStems(List<CreateWordStem> wordStemsInit) {
        List<WordStem> wordStems = new ArrayList<>();
        for (CreateWordStem createWordStem : wordStemsInit) {
            wordStems.add(dtoWordstemToEntityWordstem(createWordStem));
            log.info("The new word stem: \"" + createWordStem.getWordStemName() + "\" has been added.");
        }
        wordStemRepository.saveAll(wordStems);
    }

    private WordStem dtoWordstemToEntityWordstem(CreateWordStem createWordStem) {
        WordStem wordStem = new WordStem();
        wordStem.setWordStemName(createWordStem.getWordStemName());
        wordStem.setWordStemLanguage(createWordStem.getWordStemLanguage());
        wordStem.setGender(createWordStem.getGender());
        wordStem.setWordClass(createWordStem.getWordClass());
        wordStem.setDescrEng(createWordStem.getDescrEng());
        wordStem.setDescrFr(createWordStem.getDescrFr());
        wordStem.setReferenceWordsEng(createWordStem.getReferenceWordsEng());
        wordStem.setReferenceWordsFr(createWordStem.getReferenceWordsFr());
        wordStem.setPhonetic(createWordStem.getPhonetic());
        if (createWordStem.getSemanticField() != null) {
            wordStem.setSemanticField(semanticFieldRepository
                    .findSemanticFieldBySemFieldNameEng(createWordStem.getSemanticField()).get());
        }

        if (createWordStem.getQuotes() != null) {
            Set<Quote> quotes = new HashSet<>();
            for (int i = 0; i < createWordStem.getQuotes().size(); i++) {
                quotes.add(addQuote(createWordStem.getQuotes().get(i), createWordStem.getSources().get(i)));
            }
            wordStem.setQuotes(quotes);
        }
        if (createWordStem.getSources() != null) {
            Set<Source> sources = new HashSet<>();
            for (int i = 0; i < createWordStem.getSources().size(); i++) {
                sources.add(sourceRepository.findSourceByAbbreviation(createWordStem.getSources().get(i)).get());
            }
            wordStem.setSources(sources);
        }
        /*
         * if (createWordStem.getParentsWordStemStr() != null) {
         * for (String parentStr : createWordStem.getParentsWordStemStr()) {
         * wordStem.getParents().add(wordStemRepository.findByWordStemName(parentStr).
         * get());
         * }
         * }
         */

        return wordStem;
    }

    /**
     * saveImage(MultipartFile file, long properNounId) update or create the image
     * linked to the properNouns
     */
    public void saveImage(MultipartFile file, long properNounId) {
        ProperNoun properNoun = properNounRepository.findById(properNounId).get();
        try {
            properNoun.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        properNounRepository.save(properNoun);
    }

    /**
     * addQuote() save a new quote in DB and return it
     */
    @Override
    public Quote addQuote(String quoteStr, String source) {
        Quote quote = new Quote();
        quote.setQuoteText(quoteStr);
        Optional<Source> optSource = sourceRepository.findSourceByAbbreviation(source);
        if (optSource.isPresent()) {
            quote.setSource(optSource.get());
            quoteRepository.save(quote);
            log.info("A quote for the source: \"" + quote.getSource().getSourceNameInEnglish() + "\" has been added.");
            return quote;
        } else
            throw new RuntimeException(
                    "The source " + source + " of the new quote: \"" + quoteStr + "\" doesn't exist.");
    }

    /**
     * getWordStemByID() returns the specified wordStem according to his ID in DB
     */
    @Override
    public WordstemFullDTO getWordStemByName(String id) {
        Optional<WordStem> wordStem = wordStemRepository.findByWordStemName(id);
        if (wordStem.isPresent()) {
            String parent = null;
            if (!wordStem.get().getParents().isEmpty()) {
                parent = wordStem.get().getParents().stream().findFirst().get().getWordStemName();
            }
            List<String> sources = new ArrayList<>();
            for (Source source : wordStem.get().getSources()) {
                sources.add(source.getSourceNameInOriginalLanguage() + " (" + source.getAbbreviation() + ") ");
            }
            ;

            WordstemFullDTO wordstemFullDTO = new WordstemFullDTO();
            wordstemFullDTO.setWordStemLanguage(wordStem.get().getWordStemLanguage().toString());
            wordstemFullDTO.setWordStemName(wordStem.get().getWordStemName());
            wordstemFullDTO.setFrTranslation(wordStem.get().getReferenceWordsFr());
            wordstemFullDTO.setEngTranslation(wordStem.get().getReferenceWordsEng());
            wordstemFullDTO.setGender(wordStem.get().getGender().toString());
            wordstemFullDTO.setPhonetic(wordStem.get().getPhonetic());
            wordstemFullDTO.setDescrFr(wordStem.get().getDescrFr());
            wordstemFullDTO.setSources(sources);
            // wordstemFullDTO.setProperNouns(wordStem.get().getEtymonNames());
            wordstemFullDTO.setParents(Arrays.asList(parent));
            wordstemFullDTO.setWordClass(wordStem.get().getWordClass().toString());
            return wordstemFullDTO;
        } else
            throw new RuntimeException("Their is no wordStem with the id:" + id);
    }

    /**
     * deleteWordStem() delete a wordStem according to the his ID in DB
     */
    @Override
    public void deleteWordStem(Long id) {
        Optional<WordStem> wordStem = wordStemRepository.findById(id);
        if (wordStem.isPresent()) {
            wordStemRepository.delete(wordStem.get());
        } else
            throw new RuntimeException("Their is no wordStem with the id: " + id + " to delete");
    }

    /**
     * * findAll returns the whole list of wordStemsWord
     **/
    @Override
    public List<WordstemBasicDTO> findAll() {
        List<WordStem> wordStems = wordStemRepository.findAll();
        List<WordstemBasicDTO> showWordstems = new ArrayList<>();
        for (WordStem wordStem : wordStems) {
            showWordstems.add(getWordstemBasicDTO(wordStem));
        }
        return showWordstems;

    }

    WordstemBasicDTO getWordstemBasicDTO(WordStem wordStem) {

        return new WordstemBasicDTO(
                wordStem.getWordStemName(),
                wordStem.getWordStemLanguage().toString(),
                wordStem.getPhonetic(),
                wordStem.getGender().toString(),
                wordStem.getWordClass().toString(),
                wordStem.getReferenceWordsEng(),
                wordStem.getReferenceWordsFr(),
                wordStem.getSemanticField().getSemFieldNameFr(),
                wordStem.getFirstOccurence());
    }

    public WordStem addWordStem(WordstemBasicDTO wordStemDTO) {
        WordStem wordStem = new WordStem();
        wordStem.setWordStemName(wordStemDTO.getWordStemName());
        wordStem.setWordStemLanguage(LanguageEnum.valueOf(wordStemDTO.getWordStemLanguage()));
        wordStem.setPhonetic(wordStemDTO.getPhonetic());
        wordStem.setGender(GenderEnum.valueOf(wordStemDTO.getGender()));
        wordStem.setWordClass(WordClassEnum.valueOf(wordStemDTO.getWordClass()));
        wordStem.setReferenceWordsEng(wordStemDTO.getEngTranslation());
        wordStem.setReferenceWordsFr(wordStemDTO.getFrTranslation());
        wordStem.setSemanticField(
                semanticFieldRepository.findById(Long.parseLong(wordStemDTO.getSemanticField())).get());
        wordStem.setFirstOccurence(wordStemDTO.getFirstOccurence());

        return wordStemRepository.save(wordStem);
    }

    public void addSemanticField(SemanticField semanticField) {
        semanticFieldRepository.save(semanticField);
    }

    public void saveAllSemanticField(List<SemanticField> semanticFields) {
        semanticFieldRepository.saveAll(semanticFields);
    }

    /**
     * getStatisticInfo() returns a simple count of each etymon (proper name)
     * registered in DB and group by themes
     */
    public StatisticDTO getStatisticInfo() {
        StatisticDTO statisticDTO = new StatisticDTO();
        statisticDTO.setPlacesCount(properNounRepository.findAllProperNounsByWordTheme(1).size());
        statisticDTO.setHistoristicFiguresCount(properNounRepository.findAllProperNounsByWordTheme(2).size());
        statisticDTO.setMythicFiguresCount(properNounRepository.findAllProperNounsByWordTheme(3).size());
        statisticDTO.setTribesCount(properNounRepository.findAllProperNounsByWordTheme(4).size());
        statisticDTO.setObjectsCount(properNounRepository.findAllProperNounsByWordTheme(5).size());
        statisticDTO.setWordStemsCount((int) wordStemRepository.count());
        System.out.println(
                " WordStems count: " + statisticDTO.getWordStemsCount() +
                        "\n Placescount: " + statisticDTO.getPlacesCount() +
                        "\n Historcic figures count: " + statisticDTO.getHistoristicFiguresCount() +
                        "\n Mythic figures count: " + statisticDTO.getMythicFiguresCount() +
                        "\n Tribes count: " + statisticDTO.getTribesCount() +
                        "\n Obects count: " + statisticDTO.getObjectsCount());
        return statisticDTO;
    }

    /**
     * getAllWordStems() returns all the wordStems present in the database
     */

    @Override
    public List<String> getWordStemsPCStringList() {
        List<String> wordStemsStr = new ArrayList<>();
        for (WordStem wordStem : wordStemRepository.findAll()) {
            if (wordStem.getWordStemLanguage() == LanguageEnum.LPC) {
                wordStemsStr.add(wordStem.getWordStemName());
            }
        }
        return wordStemsStr;
    }

    public void setWordStemQuoteLink(Quote quote, String wordStemStr) {
        Optional<WordStem> wordStem = wordStemRepository.findByWordStemName(wordStemStr);
        if (wordStem.isPresent()) {
            List<WordStem> wordStems = quote.getWordStems();
            wordStems.add(wordStem.get());
            quote.setWordStems(wordStems);
            quoteRepository.save(quote);
        } else
            throw new IllegalArgumentException("the wordStem: " + wordStemStr + " doesn't exist in DB.");
    }

}
