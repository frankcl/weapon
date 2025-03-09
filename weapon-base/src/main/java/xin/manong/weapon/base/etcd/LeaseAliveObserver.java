package xin.manong.weapon.base.etcd;

import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 租约活性观察者
 *
 * @author frankcl
 * @date 2025-03-09 19:26:16
 */
public class LeaseAliveObserver implements StreamObserver<LeaseKeepAliveResponse> {

    private static final Logger logger = LoggerFactory.getLogger(LeaseAliveObserver.class);

    protected final Long leaseID;

    public LeaseAliveObserver(Long leaseID) {
        this.leaseID = leaseID;
    }

    @Override
    public void onNext(LeaseKeepAliveResponse leaseKeepAliveResponse) {
        logger.info("lease[{}] is alive for remaining TTL[{}]", leaseKeepAliveResponse.getID(),
                leaseKeepAliveResponse.getTTL());
    }

    @Override
    public void onError(Throwable throwable) {
        logger.error("error occurred when keeping alive for lease[{}], cause[{}]", leaseID, throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        logger.info("lease[{}] keep alive completed", leaseID);
    }
}
