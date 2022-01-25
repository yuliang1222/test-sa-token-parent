package cn.dev33.satoken.sso;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;

public class SaSsoTemplate {
	public StpLogic stpLogic;

	public SaSsoTemplate(StpLogic stpLogic) {
		this.stpLogic = stpLogic;
	}

	// ---------------------- Ticket 操作 ----------------------

	public String randomTicket(Object loginId) {
		return SaFoxUtil.getRandomString(64);
	}

	public void saveTicket(String ticket, Object loginId) {
		long ticketTimeout = SaManager.getConfig().getSso().getTicketTimeout();
		SaManager.getSaTokenDao().set(splicingTicketSaveKey(ticket), String.valueOf(loginId), ticketTimeout);
	}

	/**
	 * 拼接key：Ticket 查 账号Id
	 *
	 * @param ticket
	 * @return
	 */
	public String splicingTicketSaveKey(String ticket) {
		return SaManager.getConfig().getTokenName() + ":ticket:" + ticket;
	}

	/**
	 * 拼接key：账号Id 反查 Ticket
	 *
	 * @param id 账号id
	 * @return key
	 */
	public String splicingTicketIndexKey(Object id) {
		return SaManager.getConfig().getTokenName() + ":id-ticket:" + id;
	}

	/**
	 * 保存 Ticket 索引
	 *
	 * @param ticket  ticket码
	 * @param loginId 账号id
	 */
	public void saveTicketIndex(String ticket, Object loginId) {
		long ticketTimeout = SaManager.getConfig().getSso().getTicketTimeout();
		SaManager.getSaTokenDao().set(splicingTicketIndexKey(loginId), String.valueOf(ticket), ticketTimeout);
	}

	/**
	 * 删除 Ticket
	 *
	 * @param ticket Ticket码
	 */
	public void deleteTicket(String ticket) {
		if (ticket == null) {
			return;
		}
		SaManager.getSaTokenDao().delete(splicingTicketSaveKey(ticket));
	}

	/**
	 * 删除 Ticket索引
	 *
	 * @param loginId 账号id
	 */
	public void deleteTicketIndex(Object loginId) {
		if (loginId == null) {
			return;
		}
		SaManager.getSaTokenDao().delete(splicingTicketIndexKey(loginId));
	}

	/**
	 * 根据 账号id 创建一个 Ticket码
	 *
	 * @param loginId 账号id
	 * @return Ticket码
	 */
	public String createTicket(Object loginId) {
		// 创建 Ticket
		String ticket = randomTicket(loginId);

		// 保存 Ticket
		saveTicket(ticket, loginId);
		saveTicketIndex(ticket, loginId);

		// 返回 Ticket
		return ticket;
	}

	public Object getLoginId(String ticket) {
		if (SaFoxUtil.isEmpty(ticket)) {
			return null;
		}
		return SaManager.getSaTokenDao().get(splicingTicketSaveKey(ticket));
	}

	/**
	 * 根据 Ticket码 获取账号id，并转换为指定类型
	 *
	 * @param <T>    要转换的类型
	 * @param ticket Ticket码
	 * @param cs     要转换的类型
	 * @return 账号id
	 */
	public <T> T getLoginId(String ticket, Class<T> cs) {
		return SaFoxUtil.getValueByType(getLoginId(ticket), cs);
	}

	/**
	 * 查询 指定账号id的 Ticket值
	 *
	 * @param loginId 账号id
	 * @return Ticket值
	 */
	public String getTicketValue(Object loginId) {
		if (loginId == null) {
			return null;
		}
		return SaManager.getSaTokenDao().get(splicingTicketIndexKey(loginId));
	}

	/**
	 * 校验ticket码，获取账号id，如果此ticket是有效的，则立即删除
	 *
	 * @param ticket Ticket码
	 * @return 账号id
	 */
	public Object checkTicket(String ticket) {
		Object loginId = getLoginId(ticket);
		if (loginId != null) {
			deleteTicket(ticket);
			deleteTicketIndex(loginId);
		}
		return loginId;
	}
	// ---------------------- 构建URL ----------------------
























}
