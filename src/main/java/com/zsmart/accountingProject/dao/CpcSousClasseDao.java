package com.zsmart.accountingProject.dao;
import com.zsmart.accountingProject.bean.SousClasseComptable;
import com.zsmart.accountingProject.bean.Cpc;
import com.zsmart.accountingProject.bean.CpcSousClasse;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
 public interface CpcSousClasseDao extends JpaRepository<CpcSousClasse,Long> {


	 public CpcSousClasse findBySousClasseComptable(SousClasseComptable sousClasseComptable);
	 public CpcSousClasse findByCpc(Cpc cpc);
//	 public List<CpcSousClasse> findByCpc(Cpc cpc);

	 public int deleteBySousClasseComptable(SousClasseComptable sousClasseComptable);
	 public int deleteByCpc(Cpc cpc);
	 public int deleteByCpcId (Long id);
	 @Query("SELECT NEW com.zsmart.accountingProject.bean.CpcSousClasse(c.compteComptable.sousClasseComptable,SUM(c.montant)) FROM CpcCompteComptable c " +
			 "WHERE c.compteComptable.sousClasseComptable.numero=6 or c.compteComptable.sousClasseComptable.numero=7" +
			 " GROUP BY c.compteComptable.sousClasseComptable ")
	 public List<CpcSousClasse> findAllCpcSousClasse();
	

}
