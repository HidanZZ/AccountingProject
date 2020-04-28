
package com.zsmart.accountingProject.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zsmart.accountingProject.bean.Cpc;
import com.zsmart.accountingProject.bean.CpcSousClasse;
import com.zsmart.accountingProject.dao.CpcCompteComptableDao;
import com.zsmart.accountingProject.dao.CpcDao;
import com.zsmart.accountingProject.dao.CpcSousClasseDao;
import com.zsmart.accountingProject.service.facade.CpcService;
import com.zsmart.accountingProject.service.facade.CpcSousClasseService;
import com.zsmart.accountingProject.service.facade.OperationComptableService;
import com.zsmart.accountingProject.service.util.ListUtil;
import com.zsmart.accountingProject.service.util.SearchUtil;

@Service

public class CpcServiceImpl implements CpcService {
	
	
	@Autowired
	CpcCompteComptableDao cpcCompteComptableDao;
	@Autowired

	CpcSousClasseDao cpcSousClasseDao;
	@Autowired

	OperationComptableService operationComptableService;

	@Autowired

	private CpcDao cpcDao;

	@Autowired

	private EntityManager entityManager;

	@Autowired

	private CpcSousClasseService cpcsousclasseService;

	@Override
	public Cpc findCpcClasseComptable(Date dateDebut, Date dateFin) {

		List<Object[]> classeProduits = operationComptableService.findGroupeByClasseCompteComptable(dateDebut, dateFin,
				"7");
		List<Object[]> classeCharges = operationComptableService.findGroupeByClasseCompteComptable(dateDebut, dateFin,
				"6");
		Cpc cpc = new Cpc();
		if (ListUtil.isEmpty(classeCharges)) {
			cpc.setTotalProduit(BigDecimal.ZERO);
		} else {
			Object[] chargOperationComptableCharge = classeCharges.get(0);
			cpc.setTotalCharge((BigDecimal) chargOperationComptableCharge[1]);
			
		}
		if (ListUtil.isEmpty(classeProduits)) {
			cpc.setTotalCharge(BigDecimal.ZERO);
		} else {
			Object[] chargOperationComptableProduit = classeProduits.get(0);
			cpc.setTotalProduit((BigDecimal) chargOperationComptableProduit[1]);
		}
		cpc.setDateDebut(dateDebut);
		cpc.setDateFin(dateFin);
		cpc.setResultat(cpc.getTotalProduit().subtract(cpc.getTotalCharge()));

		cpc.setCpcSousClasses(cpcsousclasseService.findCpcSousClasseComptable(dateDebut, dateFin));
		return cpc;
	}

	@Override
	public Cpc save(Cpc cpc) {

		if (cpc == null) {
			return null;
		} else {
			cpcDao.save(cpc);
			return cpc;
		}
	}

	@Override
	public Cpc saveWithCpcSousClasses(Cpc cpc) {

		if (cpc == null) {
			return null;
		} else {
			if (ListUtil.isEmpty(cpc.getCpcSousClasses())) {
				return null;
			} else {
				cpcDao.save(cpc);
				for (CpcSousClasse cpcsousclasse : cpc.getCpcSousClasses()) {
					cpcsousclasse.setCpc(cpc);
					cpcsousclasseService.saveWithCpcCompteComptables(cpcsousclasse);
				}
				return cpc;
			}
		}
	}

	@Override
	public List<Cpc> findAll() {
		return cpcDao.findAll();
	}

	@Override
	public Cpc findById(Long id) {
		return cpcDao.getOne(id);
	}

	@Override
	public int delete(Cpc cpc) {
		if (cpc == null) {
			return -1;
		} else {
			
			cpcDao.delete(cpc);
			return 1;
		}
	}
	
	@Transactional
	@Override
	public void deleteById(Long id) {
//		
//		deleteCpc("CpcCompteComptable","cpcSousClasse.cpc.id", id);
//		deleteCpc("CpcSousClasse","cpc.id", id);
		cpcCompteComptableDao.deleteByCpcSousClasseCpcId(id);
		cpcSousClasseDao.deleteByCpcId(id);
		cpcDao.deleteById(id);
		
	}
	
//	private int deleteCpc(String table, String column, Long id) {
//		String query = "DELETE FROM "+table+" c  WHERE c."+column+" = "+id;
//				return entityManager.createQuery(query).executeUpdate();
//	} 

	public void clone(Cpc cpc, Cpc cpcClone) {
		if (cpc != null && cpcClone != null) {
			cpcClone.setId(cpc.getId());
			cpcClone.setDateDebut(cpc.getDateDebut());
			cpcClone.setDateFin(cpc.getDateFin());
			cpcClone.setReferenceSociete(cpc.getReferenceSociete());
			cpcClone.setTotalCharge(cpc.getTotalCharge());
			cpcClone.setTotalProduit(cpc.getTotalProduit());
			cpcClone.setResultat(cpc.getResultat());
			cpcClone.setCpcSousClasses(cpcsousclasseService.clone(cpc.getCpcSousClasses()));
		}
	}

	public Cpc clone(Cpc cpc) {
		if (cpc == null) {
			return null;
		} else {
			Cpc cpcClone = new Cpc();
			clone(cpc, cpcClone);
			return cpcClone;
		}
	}

	public List<Cpc> clone(List<Cpc> cpcs) {
		if (cpcs == null) {
			return null;
		} else {
			List<Cpc> cpcsClone = new ArrayList();
			cpcs.forEach((cpc) -> {
				cpcsClone.add(clone(cpc));
			});
			return cpcsClone;
		}
	}

	@Override
	public List<Cpc> findByCriteria(String referenceSociete, Long idMin, Long idMax, Date dateDebutMin,
			Date dateDebutMax, Date dateFinMin, Date dateFinMax, BigDecimal totalChargeMin, BigDecimal totalChargeMax,
			BigDecimal totalProduitMin, BigDecimal totalProduitMax, BigDecimal resultatMin, BigDecimal resultatMax) {
		return entityManager.createQuery(
				constructQuery(referenceSociete, idMin, idMax, dateDebutMin, dateDebutMax, dateFinMin, dateFinMax,
						totalChargeMin, totalChargeMax, totalProduitMin, totalProduitMax, resultatMin, resultatMax))
				.getResultList();
	}

	private String constructQuery(String referenceSociete, Long idMin, Long idMax, Date dateDebutMin, Date dateDebutMax,
			Date dateFinMin, Date dateFinMax, BigDecimal totalChargeMin, BigDecimal totalChargeMax,
			BigDecimal totalProduitMin, BigDecimal totalProduitMax, BigDecimal resultatMin, BigDecimal resultatMax) {
		String query = "SELECT c FROM Cpc c where 1=1 ";
		query += SearchUtil.addConstraint("c", "referenceSociete", "=", referenceSociete);
		query += SearchUtil.addConstraintMinMax("c", "id", idMin, idMax);
		query += SearchUtil.addConstraintMinMaxDate("c", " dateDebut", dateDebutMin, dateDebutMax);
		query += SearchUtil.addConstraintMinMaxDate("c", " dateFin", dateFinMin, dateFinMax);
		query += SearchUtil.addConstraintMinMax("c", "totalCharge", totalChargeMin, totalChargeMax);
		query += SearchUtil.addConstraintMinMax("c", "totalProduit", totalProduitMin, totalProduitMax);
		query += SearchUtil.addConstraintMinMax("c", "resultat", resultatMin, resultatMax);

		return query;
	}

}
