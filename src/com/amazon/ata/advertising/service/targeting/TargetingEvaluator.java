package com.amazon.ata.advertising.service.targeting;

import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicate;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Evaluates TargetingPredicates for a given RequestContext.
 */
public class TargetingEvaluator {
    public static final boolean IMPLEMENTED_STREAMS = true;
    public static final boolean IMPLEMENTED_CONCURRENCY = false;
    private final RequestContext requestContext;

    /**
     * Creates an evaluator for targeting predicates.
     * @param requestContext Context that can be used to evaluate the predicates.
     */
    public TargetingEvaluator(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    /**
     * Evaluate a TargetingGroup to determine if all of its TargetingPredicates are TRUE or not for the given
     * RequestContext.
     * @param targetingGroup Targeting group for an advertisement, including TargetingPredicates.
     * @return TRUE if all of the TargetingPredicates evaluate to TRUE against the RequestContext, FALSE otherwise.
     */
   /* public TargetingPredicateResult evaluate(TargetingGroup targetingGroup) {
        List<TargetingPredicate> targetingPredicates = targetingGroup.getTargetingPredicates();
        boolean allTruePredicates = true;
        for (TargetingPredicate predicate : targetingPredicates) {
            TargetingPredicateResult predicateResult = predicate.evaluate(requestContext);
            if (!predicateResult.isTrue()) {
                allTruePredicates = false;
                break;
            }
        }

        return allTruePredicates ? TargetingPredicateResult.TRUE :
                                   TargetingPredicateResult.FALSE;
*/

   /* public TargetingPredicateResult evaluate(TargetingGroup targetingGroup) {
        List<TargetingPredicate> targetingPredicates = targetingGroup.getTargetingPredicates();
        boolean allTruePredicates = true;
        for (TargetingPredicate predicate : targetingPredicates) {
            TargetingPredicateResult predicateResult = predicate.evaluate(requestContext);
            if (!predicateResult.isTrue()) {
                allTruePredicates = false;
                break;
            }
        }

        return allTruePredicates ? TargetingPredicateResult.TRUE :
                TargetingPredicateResult.FALSE;
    }*/

    //Update TargetingEvaluator's evaluate method to use a stream instead of a for loop to evaluate the TargetingPredicateResult.
    // This will improve the performance of the method and make it more efficient for large numbers of predicates.
    public TargetingPredicateResult evaluate(TargetingGroup targetingGroup) {
        List<TargetingPredicate> targetingPredicates = targetingGroup.getTargetingPredicates();
        return targetingPredicates.stream()
               .map(predicate -> predicate.evaluate(requestContext))
               .reduce(TargetingPredicateResult.TRUE, (result1, result2) -> result1.isTrue() && result2.isTrue()? TargetingPredicateResult.TRUE : TargetingPredicateResult.FALSE);


        // If the implementation supports Java 8 Streams, you can also use parallel streams for better performance:
        // return targetingPredicates.parallelStream()
        //       .map(predicate -> predicate.evaluate(requestContext))
        //       .reduce(TargetingPredicateResult.TRUE, (result1, result2) -> result1.isTrue() && result2.isTrue()? TargetingPredicateResult.TRUE : TargetingPredicateResult.FALSE);
        // Note: In a real-world scenario, you might want to consider using a more efficient data structure or algorithm for parallel processing.
        // For example, if the TargetingPredicates are stored in a database, you might consider using a database query to evaluate them in parallel.
        // For more information on parallel processing in Java 8, see the official Java documentation: https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
    }



}
