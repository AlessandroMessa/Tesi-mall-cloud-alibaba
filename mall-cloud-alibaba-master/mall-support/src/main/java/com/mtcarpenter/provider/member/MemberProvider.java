package com.mtcarpenter.provider.member;

import com.mtcarpenter.mall.client.member.query.MemberAddressQueryClient;
import com.mtcarpenter.mall.model.UmsMember;
import com.mtcarpenter.mall.model.UmsMemberReceiveAddress;
import com.mtcarpenter.mall.util.MemberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MemberProvider {

    @Autowired
    private MemberUtil memberUtil;
    @Autowired
    private MemberAddressQueryClient memberAddressQueryClient;

    public UmsMember getCurrentMember(HttpServletRequest request) {
        return memberUtil.getRedisUmsMember(request);
    }

    public List<UmsMemberReceiveAddress> listAddresses(Long memberId) {
        return memberAddressQueryClient.list(memberId).getData();
    }

    public UmsMemberReceiveAddress getAddress(Long addressId) {
        return memberAddressQueryClient.getItem(addressId).getData();
    }
}
