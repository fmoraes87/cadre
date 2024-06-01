do $$

DECLARE
seqName varchar(255);
idColumnName varchar(255);
maxValue numeric(10,0);
myTable cadre.ad_table%ROWTYPE;
seqexists numeric;

BEGIN


	FOR myTable IN SELECT * FROM cadre.AD_Table where isVIew='N' and tablename not like '%_Trl' order by ad_extension_id,name LOOP
		seqName := lower(myTable.tableName)||'_sq';
		idColumnName := myTable.tableName||'_id';

		EXECUTE 'SELECT coalesce(max(' || idColumnName ||' ),0)+1 FROM cadre.'|| myTable.tableName INTO maxValue;

		if maxValue > 0 then

			EXECUTE 'SELECT count(1) FROM pg_class where LOWER(relname) = '|| quote_literal(seqName)  INTO seqexists;

			if (seqexists = 0) then
				RAISE NOTICE 'CREATE SEQUENCE cadre.% INCREMENT BY 1 MINVALUE 1 START WITH %', seqName,maxValue;
				EXECUTE 'CREATE SEQUENCE cadre.' || seqName || ' INCREMENT BY 1 MINVALUE 1 START WITH ' || maxValue;
			else
				RAISE NOTICE  'ALTER SEQUENCE cadre.% RESTART WITH %', seqName, maxValue ;
				EXECUTE  'ALTER SEQUENCE cadre.' || seqName || ' RESTART WITH ' || maxValue ;
			end if;
		end if;
	END LOOP;

end $$;
