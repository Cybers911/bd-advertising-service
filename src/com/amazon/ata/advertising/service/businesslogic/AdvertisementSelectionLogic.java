package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * This class is responsible for picking the advertisement to be rendered.
 */
public class AdvertisementSelectionLogic {

    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);

    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
    private Random random = new Random();

    /**
     * Constructor for AdvertisementSelectionLogic.
     * @param contentDao Source of advertising content.
     * @param targetingGroupDao Source of targeting groups for each advertising content.
     */
    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;
    }

    /**
     * Setter for Random class.
     * @param random generates random number used to select advertisements.
     */
    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Gets all of the content and metadata for the marketplace and determines which content can be shown.  Returns the
     * eligible content with the highest click through rate.  If no advertisement is available or eligible, returns an
     * EmptyGeneratedAdvertisement.
     *
     * @param customerId - the customer to generate a custom advertisement for
     * @param marketplaceId - the id of the marketplace the advertisement will be rendered on
     * @return an advertisement customized for the customer id provided, or an empty advertisement if one could
     *     not be generated.
     */
   /* public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        GeneratedAdvertisement generatedAdvertisement = new EmptyGeneratedAdvertisement();
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.warn("MarketplaceId cannot be null or empty. Returning empty ad.");
        } else {
            final List<AdvertisementContent> contents = contentDao.get(marketplaceId);

            if (CollectionUtils.isNotEmpty(contents)) {
                AdvertisementContent randomAdvertisementContent = contents.get(random.nextInt(contents.size()));
                generatedAdvertisement = new GeneratedAdvertisement(randomAdvertisementContent);
            }*/

    /*public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        GeneratedAdvertisement generatedAdvertisement = new EmptyGeneratedAdvertisement();
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.warn("MarketplaceId cannot be null or empty. Returning empty ad.");
        } else {
            TargetingEvaluator targetingEvaluator = new TargetingEvaluator(new RequestContext(customerId, marketplaceId));

            final TreeMap<Double, AdvertisementContent> sortedAds = new TreeMap<>(Comparator.reverseOrder());

            contentDao.get(marketplaceId)
                    .forEach(content -> targetingGroupDao.get(content.getContentId()).stream()
                            .filter(targetingGroup -> targetingEvaluator.evaluate(targetingGroup).isTrue())
                            .map(TargetingGroup::getClickThroughRate)
                            .max(Double::compareTo)
                            .ifPresent(ctr -> sortedAds.put(ctr,content)));

            if (!sortedAds.isEmpty()) {
                generatedAdvertisement = new GeneratedAdvertisement(sortedAds.firstEntry().getValue());
            }

        }

        return generatedAdvertisement;
    }*/

   /* public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.warn("MarketplaceId cannot be null or empty. Returning empty ad.");
            return new EmptyGeneratedAdvertisement();
        }

        return Optional.ofNullable(contentDao.get(marketplaceId))
                .filter(CollectionUtils::isNotEmpty)
                .map(contents -> contents.get(random.nextInt(contents.size())))
                .map(GeneratedAdvertisement::new)
                .orElseGet(EmptyGeneratedAdvertisement::new);
    }*/
/*
    public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.warn("MarketplaceId cannot be null or empty. Returning empty ad.");
            return new EmptyGeneratedAdvertisement();
        }

        List<AdvertisementContent> eligibleAds = Optional.ofNullable(contentDao.get(marketplaceId))
                .orElse(Collections.emptyList())
                .stream()
                .filter(ad -> CollectionUtils.isNotEmpty(targetingGroupDao.get(ad.getContentId())))
                .filter(ad -> targetingGroupDao.get(ad.getContentId()).stream()
                        .anyMatch(group -> targetingEvaluator.evaluate(group, new RequestContext(customerId, marketplaceId))))
                .collect(Collectors.toList());

        return eligibleAds.isEmpty()
                ? new EmptyGeneratedAdvertisement()
                : new GeneratedAdvertisement(eligibleAds.get(random.nextInt(eligibleAds.size())));
    }*/

    /*public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.warn("MarketplaceId cannot be null or empty. Returning empty ad.");
            return new EmptyGeneratedAdvertisement();
        }

        TargetingEvaluator targetingEvaluator = new TargetingEvaluator(new RequestContext(customerId, marketplaceId));

        List<AdvertisementContent> eligibleAds = Optional.ofNullable(contentDao.get(marketplaceId))
                .orElse(Collections.emptyList())
                .stream()
                .filter(ad -> {
                    List<TargetingGroup> targetingGroups = targetingGroupDao.get(ad.getContentId());
                    return CollectionUtils.isNotEmpty(targetingGroups) &&
                            targetingGroups.stream().anyMatch(group -> targetingEvaluator.evaluate(group).isTrue());
                })
                .collect(Collectors.toList());

        if (eligibleAds.isEmpty()) {
            return new EmptyGeneratedAdvertisement();
        }

        // Sort eligible ads by click-through rate in descending order
        eligibleAds.sort((ad1, ad2) -> {
            double ctr1 = getHighestClickThroughRate(ad1.getContentId());
            double ctr2 = getHighestClickThroughRate(ad2.getContentId());
            return Double.compare(ctr2, ctr1);
        });

        // Select the ad with the highest click-through rate
        return new GeneratedAdvertisement(eligibleAds.get(0));
    }

    private double getHighestClickThroughRate(String contentId) {
        return targetingGroupDao.get(contentId).stream()
                .mapToDouble(TargetingGroup::getClickThroughRate)
                .max()
                .orElse(0.0);
    }*/


  /*  public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        return Optional.ofNullable(marketplaceId)
                .filter(id -> !StringUtils.isEmpty(id))
                .map(id -> {
                    List<AdvertisementContent> contents = contentDao.get(id);
                    return Optional.of(contents)
                            .filter(CollectionUtils::isNotEmpty)
                            .map(list -> list.stream()
                                    .skip(random.nextInt(list.size()))
                                    .findFirst()
                                    .map(GeneratedAdvertisement::new)
                                    .orElseGet(EmptyGeneratedAdvertisement::new))
                            .orElseGet(EmptyGeneratedAdvertisement::new);
                })
                .orElseGet(() -> {
                    LOG.warn("MarketplaceId cannot be null or empty. Returning empty ad.");
                    return new EmptyGeneratedAdvertisement();
                });
    }*/

    public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.warn("MarketplaceId cannot be null or empty. Returning empty ad.");
            return new EmptyGeneratedAdvertisement();
        }
    
        List<AdvertisementContent> contents = contentDao.get(marketplaceId);
        if (CollectionUtils.isEmpty(contents)) {
            return new EmptyGeneratedAdvertisement();
        }
    
        RequestContext requestContext = new RequestContext(customerId, marketplaceId);
        TargetingEvaluator evaluator = new TargetingEvaluator(requestContext);
    
        Optional<AdvertisementContent> eligibleAd = contents.stream()
                .filter(content -> {
                    List<TargetingGroup> targetingGroups = targetingGroupDao.get(content.getContentId());
                    return targetingGroups.stream()
                            .anyMatch(group -> evaluator.evaluate(group).isTrue());
                })
                .findAny();
    
        return eligibleAd
                .map(ad -> new GeneratedAdvertisement(ad))
                .orElse(new EmptyGeneratedAdvertisement());
    }

}
