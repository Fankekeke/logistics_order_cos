package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.MerchantInfo;
import cc.mrbird.febs.cos.entity.StaffInfo;
import cc.mrbird.febs.cos.service.IMerchantInfoService;
import cc.mrbird.febs.cos.service.IStaffInfoService;
import cc.mrbird.febs.system.service.UserService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/staff-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffInfoController {

    private final IStaffInfoService staffInfoService;

    private final IMerchantInfoService merchantInfoService;

    private final UserService userService;

    /**
     * 分页获取员工信息
     *
     * @param page      分页对象
     * @param staffInfo 员工信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<StaffInfo> page, StaffInfo staffInfo) {
        return R.ok(staffInfoService.selectStaffPage(page, staffInfo));
    }

    /**
     * 获取ID获取员工详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(staffInfoService.getById(id));
    }

    /**
     * 获取员工信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(staffInfoService.list());
    }

    /**
     * 根据用户ID获取所属商家信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/selectMerchantByStaffId")
    public R selectMerchantByStaffId(Integer userId) throws FebsException {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, userId));
        if (staffInfo == null) {
            throw new FebsException("员工信息不存在");
        }
        return R.ok(merchantInfoService.getById(staffInfo.getCanteenId()));
    }

    /**
     * 根据用户ID获取员工信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/queryStaffByUserId")
    public R queryStaffByUserId(Integer userId) {
        return R.ok(staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, userId)));
    }

    /**
     * 新增员工信息
     *
     * @param staffInfo 员工信息
     * @return 结果
     */
    @PostMapping
    public R save(StaffInfo staffInfo) throws Exception {
        // 获取所属药店
        MerchantInfo merchantInfo = merchantInfoService.getOne(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getUserId, staffInfo.getCanteenId()));
        if (merchantInfo != null) {
            staffInfo.setCanteenId(merchantInfo.getId());
        }
        staffInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        staffInfo.setCode("STF-" + System.currentTimeMillis());
        userService.registStaff(staffInfo.getCode(), "1234qwer", staffInfo);
        return R.ok(true);
    }

    /**
     * 根据药店获取员工信息
     *
     * @param merchantUserId 药店ID
     * @return 结果
     */
    @GetMapping("/selectStaffByMerchant/{merchantUserId}")
    public R selectStaffByMerchant(@PathVariable("merchantUserId") Integer merchantUserId) {
        // 获取所属药店
        MerchantInfo merchantInfo = merchantInfoService.getOne(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getUserId, merchantUserId));
        if (merchantInfo == null) {
            return R.ok(Collections.emptyList());
        }
        return R.ok(staffInfoService.list(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getCanteenId, merchantInfo.getId())));
    }

    /**
     * 获取详情信息
     *
     * @param code code
     * @return 结果
     */
    @GetMapping("/code/{code}")
    public R detail(@PathVariable("code") String code) {
        return R.ok(staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getCode, code)));
    }

    /**
     * 修改员工信息
     *
     * @param staffInfo 员工信息
     * @return 结果
     */
    @PutMapping
    public R edit(StaffInfo staffInfo) {
        return R.ok(staffInfoService.updateById(staffInfo));
    }

    /**
     * 删除员工信息
     *
     * @param ids ids
     * @return 员工信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(staffInfoService.removeByIds(ids));
    }

}
