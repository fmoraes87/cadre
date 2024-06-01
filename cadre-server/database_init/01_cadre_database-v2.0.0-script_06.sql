--
-- PostgreSQL database dump
--

-- Dumped from database version 12.1 (Debian 12.1-1.pgdg100+1)
-- Dumped by pg_dump version 12.4 (Ubuntu 12.4-1.pgdg18.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: cadre; Type: SCHEMA; Schema: -; Owner: cadre
--

CREATE SCHEMA cadre;


ALTER SCHEMA cadre OWNER TO cadre;

DROP SCHEMA public;

ALTER database cadre SET search_path = cadre;

--
-- Name: generate_create_table_statement(character varying); Type: FUNCTION; Schema: cadre; Owner: cadre
--

CREATE FUNCTION cadre.generate_create_table_statement(p_table_name character varying) RETURNS text
    LANGUAGE plpgsql
    AS $_$
DECLARE
v_table_ddl   text;
    column_record record;
BEGIN
FOR column_record IN
SELECT
    b.nspname as schema_name,
    b.relname as table_name,
    a.attname as column_name,
    pg_catalog.format_type(a.atttypid, a.atttypmod) as column_type,
    CASE WHEN
             (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid) for 128)
              FROM pg_catalog.pg_attrdef d
              WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum AND a.atthasdef) IS NOT NULL THEN
                 'DEFAULT '|| (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid) for 128)
                               FROM pg_catalog.pg_attrdef d
                               WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum AND a.atthasdef)
         ELSE
             ''
        END as column_default_value,
    CASE WHEN a.attnotnull = true THEN
             'NOT NULL'
         ELSE
             'NULL'
        END as column_not_null,
    a.attnum as attnum,
    e.max_attnum as max_attnum
FROM
    pg_catalog.pg_attribute a
        INNER JOIN
    (SELECT c.oid,
            n.nspname,
            c.relname
     FROM pg_catalog.pg_class c
              LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
     WHERE c.relname ~ ('^('||p_table_name||')$')
                AND pg_catalog.pg_table_is_visible(c.oid)
     ORDER BY 2, 3) b
    ON a.attrelid = b.oid
        INNER JOIN
    (SELECT
         a.attrelid,
         max(a.attnum) as max_attnum
     FROM pg_catalog.pg_attribute a
     WHERE a.attnum > 0
       AND NOT a.attisdropped
     GROUP BY a.attrelid) e
    ON a.attrelid=e.attrelid
WHERE a.attnum > 0
  AND NOT a.attisdropped
ORDER BY a.attnum
    LOOP
        IF column_record.attnum = 1 THEN
            v_table_ddl:='CREATE TABLE '||column_record.schema_name||'.'||column_record.table_name||' (';
ELSE
            v_table_ddl:=v_table_ddl||',';
END IF;

        IF column_record.attnum <= column_record.max_attnum THEN
            v_table_ddl:=v_table_ddl||chr(10)||
                     '    '||column_record.column_name||' '||column_record.column_type||' '||column_record.column_default_value||' '||column_record.column_not_null;
END IF;
END LOOP;

    v_table_ddl:=v_table_ddl||');';
RETURN v_table_ddl;
END;
$_$;


ALTER FUNCTION cadre.generate_create_table_statement(p_table_name character varying) OWNER TO cadre;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: ad_app; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_app (
                              ad_app_id numeric(10,0) NOT NULL,
                              ad_client_id numeric(10,0) NOT NULL,
                              ad_org_id numeric(10,0) NOT NULL,
                              isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                              created timestamp without time zone DEFAULT now() NOT NULL,
                              createdby numeric(10,0) NOT NULL,
                              updated timestamp without time zone DEFAULT now() NOT NULL,
                              updatedby numeric(10,0) NOT NULL,
                              value character varying(60) NOT NULL,
                              description character varying(255),
                              ad_app_uu character varying(36) DEFAULT NULL::character varying,
                              CONSTRAINT ad_app_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_app OWNER TO cadre;

--
-- Name: ad_app_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_app_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_app_sq OWNER TO cadre;

--
-- Name: ad_apprule; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_apprule (
                                  ad_apprule_id numeric(10,0) NOT NULL,
                                  ad_client_id numeric(10,0) NOT NULL,
                                  ad_org_id numeric(10,0) NOT NULL,
                                  isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                  created timestamp without time zone DEFAULT now() NOT NULL,
                                  createdby numeric(10,0) NOT NULL,
                                  updated timestamp without time zone DEFAULT now() NOT NULL,
                                  updatedby numeric(10,0) NOT NULL,
                                  ad_table_id numeric(10,0) NOT NULL,
                                  expression character varying(255),
                                  ad_apprule_uu character varying(36) DEFAULT NULL::character varying,
                                  ad_app_id numeric(10,0),
                                  ad_role_id numeric(10,0),
                                  CONSTRAINT ad_apprule_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_apprule OWNER TO cadre;

--
-- Name: ad_apprule_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_apprule_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_apprule_sq OWNER TO cadre;

--
-- Name: ad_attachment; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_attachment (
                                     ad_attachment_id numeric(10,0) NOT NULL,
                                     ad_client_id numeric(10,0) NOT NULL,
                                     ad_org_id numeric(10,0) NOT NULL,
                                     isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                     created timestamp without time zone DEFAULT now() NOT NULL,
                                     createdby numeric(10,0) NOT NULL,
                                     updated timestamp without time zone DEFAULT now() NOT NULL,
                                     updatedby numeric(10,0) NOT NULL,
                                     ad_media_id numeric(10,0) NOT NULL,
                                     ad_table_id numeric(10,0) NOT NULL,
                                     ad_record_id numeric(10,0) NOT NULL,
                                     ad_attachment_uu character varying(36) DEFAULT NULL::character varying,
                                     CONSTRAINT ad_attachment_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_attachment OWNER TO cadre;

--
-- Name: ad_attachment_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_attachment_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_attachment_sq OWNER TO cadre;

--
-- Name: ad_client; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_client (
                                 ad_client_id numeric(10,0) NOT NULL,
                                 ad_org_id numeric(10,0) NOT NULL,
                                 isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                 created timestamp without time zone DEFAULT now() NOT NULL,
                                 createdby numeric(10,0) NOT NULL,
                                 updated timestamp without time zone DEFAULT now() NOT NULL,
                                 updatedby numeric(10,0) NOT NULL,
                                 value character varying(40) NOT NULL,
                                 name character varying(60) NOT NULL,
                                 description character varying(255),
                                 ad_language character varying(6),
                                 ad_client_uu character varying(36) DEFAULT NULL::character varying,
                                 ad_mailconfig_id numeric(10,0),
                                 ad_tree_id numeric(10,0) DEFAULT 0 NOT NULL,
                                 CONSTRAINT ad_client_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_client OWNER TO cadre;

--
-- Name: ad_client_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_client_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_client_sq OWNER TO cadre;

--
-- Name: ad_column; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_column (
                                 ad_client_id numeric(10,0) NOT NULL,
                                 ad_column_id numeric(10,0) NOT NULL,
                                 ad_column_uu character varying(36) DEFAULT NULL::character varying,
                                 ad_org_id numeric(10,0) NOT NULL,
                                 ad_reference_id numeric(10,0) NOT NULL,
                                 ad_table_id numeric(10,0) NOT NULL,
                                 columnname character varying(30) NOT NULL,
                                 created timestamp without time zone DEFAULT now() NOT NULL,
                                 createdby numeric(10,0) NOT NULL,
                                 description character varying(255),
                                 help character varying(2000),
                                 isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                 iskey character(1) DEFAULT 'N'::bpchar NOT NULL,
                                 ismandatory character(1) DEFAULT 'N'::bpchar NOT NULL,
                                 name character varying(60) NOT NULL,
                                 updated timestamp without time zone DEFAULT now() NOT NULL,
                                 updatedby numeric(10,0) NOT NULL,
                                 istranslatable character(1) DEFAULT 'N'::bpchar NOT NULL,
                                 ad_extension_id numeric(10,0) DEFAULT 0 NOT NULL,
                                 referencevalue character varying(255),
                                 ad_reference_value_id numeric(10,0),
                                 updatable character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                 isidentifier character(1) DEFAULT 'N'::bpchar NOT NULL,
                                 CONSTRAINT ad_column_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                 CONSTRAINT ad_column_iskey_check CHECK ((iskey = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                 CONSTRAINT ad_column_ismandatory_check CHECK ((ismandatory = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                 CONSTRAINT ad_column_istranslatable_check CHECK ((istranslatable = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_column OWNER TO cadre;

--
-- Name: ad_column_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_column_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_column_sq OWNER TO cadre;

--
-- Name: ad_cronjob; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_cronjob (
                                  ad_cronjob_id numeric(10,0) NOT NULL,
                                  ad_client_id numeric(10,0) NOT NULL,
                                  ad_org_id numeric(10,0) NOT NULL,
                                  isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                  created timestamp without time zone DEFAULT now() NOT NULL,
                                  createdby numeric(10,0) NOT NULL,
                                  updated timestamp without time zone DEFAULT now() NOT NULL,
                                  updatedby numeric(10,0) NOT NULL,
                                  lastresult character(1),
                                  lastendtime timestamp without time zone,
                                  laststarttime timestamp without time zone,
                                  cronexpression character varying(255),
                                  ad_jobdefinition_id numeric(10,0) NOT NULL,
                                  ad_cronjob_uu character varying(36) DEFAULT NULL::character varying,
                                  ad_user_id numeric(10,0),
                                  currentstatus character varying(255) DEFAULT 'NEW'::character varying NOT NULL,
                                  CONSTRAINT ad_cronjob_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_cronjob OWNER TO cadre;

--
-- Name: ad_cronjob_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_cronjob_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_cronjob_sq OWNER TO cadre;

--
-- Name: ad_extension; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_extension (
                                    ad_extension_id numeric(10,0) NOT NULL,
                                    ad_client_id numeric(10,0) NOT NULL,
                                    ad_org_id numeric(10,0) NOT NULL,
                                    isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                    created timestamp without time zone DEFAULT now() NOT NULL,
                                    createdby numeric(10,0) NOT NULL,
                                    updated timestamp without time zone DEFAULT now() NOT NULL,
                                    updatedby numeric(10,0) NOT NULL,
                                    name character varying(60) NOT NULL,
                                    description character varying(255),
                                    modelproviderclass character varying(255),
                                    ad_extension_uu character varying(36) DEFAULT NULL::character varying NOT NULL,
                                    seqno numeric(10,0) NOT NULL,
                                    serviceproviderclass text,
                                    value character varying(60) NOT NULL,
                                    CONSTRAINT ad_extension_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_extension OWNER TO cadre;

--
-- Name: ad_extension_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_extension_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_extension_sq OWNER TO cadre;

--
-- Name: ad_field; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_field (
                                ad_client_id numeric(10,0) NOT NULL,
                                ad_field_id numeric(10,0) NOT NULL,
                                ad_field_uu character varying(36) DEFAULT NULL::character varying,
                                ad_org_id numeric(10,0) NOT NULL,
                                ad_tab_id numeric(10,0) NOT NULL,
                                ad_column_id numeric(10,0),
                                created timestamp without time zone DEFAULT now() NOT NULL,
                                createdby numeric(10,0) NOT NULL,
                                defaultvalue character varying(2000),
                                description character varying(255),
                                help character varying(2000),
                                isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                isdisplayed character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                isdisplayedgrid character(1) DEFAULT 'Y'::bpchar,
                                ismandatory character(1),
                                isreadonly character(1) DEFAULT 'N'::bpchar NOT NULL,
                                issameline character(1) DEFAULT 'N'::bpchar NOT NULL,
                                label character varying(60) NOT NULL,
                                placeholder character varying(255) DEFAULT NULL::character varying,
                                seqno numeric(10,0),
                                updated timestamp without time zone DEFAULT now() NOT NULL,
                                updatedby numeric(10,0) NOT NULL,
                                bootstrapclass character varying(255) DEFAULT 'col-md-6 mb-3'::character varying,
                                ad_extension_id numeric(10,0) DEFAULT 0 NOT NULL,
                                dynamicvalidation character varying(255),
                                CONSTRAINT ad_field_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_field_isdisplayed_check CHECK ((isdisplayed = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_field_isdisplayedgrid_check CHECK ((isdisplayedgrid = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_field_ismandatory_check CHECK ((ismandatory = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_field_isreadonly_check CHECK ((isreadonly = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_field_issameline_check CHECK ((issameline = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_field OWNER TO cadre;

--
-- Name: ad_field_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_field_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_field_sq OWNER TO cadre;

--
-- Name: ad_jobdefinition; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_jobdefinition (
                                        ad_jobdefinition_id numeric(10,0) NOT NULL,
                                        ad_client_id numeric(10,0) NOT NULL,
                                        ad_org_id numeric(10,0) NOT NULL,
                                        isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        created timestamp(6) without time zone DEFAULT now() NOT NULL,
                                        createdby numeric(10,0) NOT NULL,
                                        updated timestamp(6) without time zone DEFAULT now() NOT NULL,
                                        updatedby numeric(10,0) NOT NULL,
                                        name character varying(60) NOT NULL,
                                        description character varying(255),
                                        help character varying(255),
                                        ad_process_id numeric(10,0),
                                        ad_jobdefinition_uu character varying(36) DEFAULT NULL::character varying,
                                        procedurename character varying(255),
                                        ad_scripting_id numeric(10,0),
                                        CONSTRAINT ad_jobdefinition_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_jobdefinition OWNER TO cadre;

--
-- Name: ad_jobdefinition_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_jobdefinition_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_jobdefinition_sq OWNER TO cadre;

--
-- Name: ad_language; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_language (
                                   ad_language character varying(6) NOT NULL,
                                   ad_client_id numeric(10,0) NOT NULL,
                                   ad_org_id numeric(10,0) NOT NULL,
                                   isactive character(1) DEFAULT 'N'::bpchar NOT NULL,
                                   created timestamp without time zone DEFAULT now() NOT NULL,
                                   createdby numeric(10,0) NOT NULL,
                                   updated timestamp without time zone DEFAULT now() NOT NULL,
                                   updatedby numeric(10,0) NOT NULL,
                                   name character varying(60) NOT NULL,
                                   languageiso character(2),
                                   countrycode character(2),
                                   isbaselanguage character(1) DEFAULT 'N'::bpchar NOT NULL,
                                   issystemlanguage character(1) DEFAULT 'N'::bpchar NOT NULL,
                                   ad_language_id numeric(10,0) NOT NULL,
                                   isdecimalpoint character(1),
                                   datepattern character varying(20),
                                   timepattern character varying(20),
                                   ad_language_uu character varying(36) DEFAULT NULL::character varying,
                                   CONSTRAINT ad_language_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                   CONSTRAINT ad_language_isbaselanguage_check CHECK ((isbaselanguage = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                   CONSTRAINT ad_language_issystemlanguage_check CHECK ((issystemlanguage = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_language OWNER TO cadre;

--
-- Name: ad_language_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_language_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_language_sq OWNER TO cadre;

--
-- Name: ad_loginmodule_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_loginmodule_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_loginmodule_sq OWNER TO cadre;

--
-- Name: ad_mailconfig; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_mailconfig (
                                     ad_mailconfig_id numeric(10,0) NOT NULL,
                                     ad_client_id numeric(10,0) NOT NULL,
                                     ad_org_id numeric(10,0) NOT NULL,
                                     isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                     created timestamp without time zone DEFAULT now() NOT NULL,
                                     createdby numeric(10,0) NOT NULL,
                                     updated timestamp without time zone DEFAULT now() NOT NULL,
                                     updatedby numeric(10,0) NOT NULL,
                                     requestemail character varying(60) NOT NULL,
                                     requestfolder character varying(20) NOT NULL,
                                     requestuser character varying(60) NOT NULL,
                                     requestuserpw character varying(255) NOT NULL,
                                     smtphost character varying(60) NOT NULL,
                                     smtpport numeric(10,0) NOT NULL,
                                     ad_mailconfig_uu character varying(36) DEFAULT NULL::character varying,
                                     name character varying(60),
                                     CONSTRAINT ad_mailconfig_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_mailconfig OWNER TO cadre;

--
-- Name: ad_mailconfig_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_mailconfig_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_mailconfig_sq OWNER TO cadre;

--
-- Name: ad_media; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_media (
                                ad_media_id numeric(10,0) NOT NULL,
                                ad_client_id numeric(10,0) NOT NULL,
                                ad_org_id numeric(10,0) NOT NULL,
                                isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                created timestamp without time zone DEFAULT now() NOT NULL,
                                createdby numeric(10,0) NOT NULL,
                                updated timestamp without time zone DEFAULT now() NOT NULL,
                                updatedby numeric(10,0) NOT NULL,
                                value character varying(60) NOT NULL,
                                ad_mediaformat_id numeric(10,0) NOT NULL,
                                ad_mediafolder_id numeric(10,0) NOT NULL,
                                ad_media_uu character varying(36) DEFAULT NULL::character varying,
                                CONSTRAINT ad_media_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_media OWNER TO cadre;

--
-- Name: ad_media_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_media_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_media_sq OWNER TO cadre;

--
-- Name: ad_mediafolder; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_mediafolder (
                                      ad_mediafolder_id numeric(10,0) NOT NULL,
                                      ad_client_id numeric(10,0) NOT NULL,
                                      ad_org_id numeric(10,0) NOT NULL,
                                      isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                      created timestamp without time zone DEFAULT now() NOT NULL,
                                      createdby numeric(10,0) NOT NULL,
                                      updated timestamp without time zone DEFAULT now() NOT NULL,
                                      updatedby numeric(10,0) NOT NULL,
                                      name character varying(60) NOT NULL,
                                      method character varying(60) NOT NULL,
                                      attributes text,
                                      ad_mediafolder_uu character varying(36) DEFAULT NULL::character varying,
                                      isinternalstorage character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                      issecurityaccess character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                      CONSTRAINT ad_mediafolder_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                      CONSTRAINT ad_mediafolder_isinternalstorage_check CHECK ((isinternalstorage = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                      CONSTRAINT ad_mediafolder_issecurityaccess_check CHECK ((issecurityaccess = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_mediafolder OWNER TO cadre;

--
-- Name: ad_mediafolder_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_mediafolder_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_mediafolder_sq OWNER TO cadre;

--
-- Name: ad_mediaformat; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_mediaformat (
                                      ad_mediaformat_id numeric(10,0) NOT NULL,
                                      ad_client_id numeric(10,0) NOT NULL,
                                      ad_org_id numeric(10,0) NOT NULL,
                                      isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                      created timestamp without time zone DEFAULT now() NOT NULL,
                                      createdby numeric(10,0) NOT NULL,
                                      updated timestamp without time zone DEFAULT now() NOT NULL,
                                      updatedby numeric(10,0) NOT NULL,
                                      extension character varying(10) NOT NULL,
                                      mimetype character varying(100) NOT NULL,
                                      description character varying(255),
                                      ad_mediaformat_uu character varying(36) DEFAULT NULL::character varying,
                                      CONSTRAINT ad_mediaformat_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_mediaformat OWNER TO cadre;

--
-- Name: ad_mediaformat_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_mediaformat_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_mediaformat_sq OWNER TO cadre;

--
-- Name: ad_message; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_message (
                                  ad_message_id numeric(10,0) NOT NULL,
                                  ad_client_id numeric(10,0) NOT NULL,
                                  ad_org_id numeric(10,0) NOT NULL,
                                  isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                  created timestamp without time zone DEFAULT now() NOT NULL,
                                  createdby numeric(10,0) NOT NULL,
                                  updated timestamp without time zone DEFAULT now() NOT NULL,
                                  updatedby numeric(10,0) NOT NULL,
                                  value character varying(255) NOT NULL,
                                  msgtext character varying(2000) NOT NULL,
                                  msgtip character varying(2000),
                                  msgtype character(1) NOT NULL,
                                  ad_message_uu character varying(36) DEFAULT NULL::character varying,
                                  CONSTRAINT ad_message_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                  CONSTRAINT ad_message_msgtype_check CHECK ((msgtype = ANY (ARRAY['I'::bpchar, 'E'::bpchar, 'W'::bpchar])))
);


ALTER TABLE cadre.ad_message OWNER TO cadre;

--
-- Name: ad_message_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_message_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_message_sq OWNER TO cadre;

--
-- Name: ad_message_trl; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_message_trl (
                                      ad_message_id numeric(10,0) NOT NULL,
                                      ad_client_id numeric(10,0) NOT NULL,
                                      ad_org_id numeric(10,0) NOT NULL,
                                      ad_language character varying(6) NOT NULL,
                                      isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                      created timestamp without time zone DEFAULT now() NOT NULL,
                                      createdby numeric(10,0) NOT NULL,
                                      updated timestamp without time zone DEFAULT now() NOT NULL,
                                      updatedby numeric(10,0) NOT NULL,
                                      msgtext character varying(2000) NOT NULL,
                                      msgtip character varying(2000),
                                      CONSTRAINT ad_message_trl_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_message_trl OWNER TO cadre;

--
-- Name: ad_message_trl_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_message_trl_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_message_trl_sq OWNER TO cadre;

--
-- Name: ad_modelvalidator; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_modelvalidator (
                                         ad_client_id numeric(10,0) DEFAULT 0 NOT NULL,
                                         ad_modelvalidator_id numeric(10,0) NOT NULL,
                                         ad_org_id numeric(10,0) DEFAULT 0 NOT NULL,
                                         created timestamp without time zone DEFAULT statement_timestamp() NOT NULL,
                                         createdby numeric(10,0) NOT NULL,
                                         updated date DEFAULT statement_timestamp() NOT NULL,
                                         updatedby numeric(10,0) NOT NULL,
                                         isactive character(1) NOT NULL,
                                         name character varying(60) NOT NULL,
                                         description character varying(255),
                                         help character varying(2000),
                                         modelvalidationclass character varying(255) NOT NULL,
                                         seqno numeric(10,0),
                                         ad_modelvalidator_uu character varying(36) DEFAULT NULL::character varying,
                                         ad_table_id numeric(10,0) NOT NULL,
                                         ad_extension_id numeric(10,0) DEFAULT 0 NOT NULL,
                                         CONSTRAINT ad_modelvalidator_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_modelvalidator OWNER TO cadre;

--
-- Name: ad_modelvalidator_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_modelvalidator_sq
    START WITH 2
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_modelvalidator_sq OWNER TO cadre;

--
-- Name: ad_notificationtemplate; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_notificationtemplate (
                                               ad_notificationtemplate_id numeric(10,0) NOT NULL,
                                               ad_client_id numeric(10,0) NOT NULL,
                                               ad_org_id numeric(10,0) NOT NULL,
                                               isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                               created timestamp without time zone DEFAULT now() NOT NULL,
                                               createdby numeric(10,0) NOT NULL,
                                               updated timestamp without time zone DEFAULT now() NOT NULL,
                                               updatedby numeric(10,0) NOT NULL,
                                               name character varying(60) NOT NULL,
                                               description character varying(255),
                                               isparsetemplate character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                               header character varying(255) NOT NULL,
                                               template text NOT NULL,
                                               ad_notificationtemplate_uu character varying(36) DEFAULT NULL::character varying,
                                               CONSTRAINT ad_notificationtemplate_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                               CONSTRAINT ad_notificationtemplate_isparsetemplate_check CHECK ((isparsetemplate = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_notificationtemplate OWNER TO cadre;

--
-- Name: ad_notificationtemplate_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_notificationtemplate_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_notificationtemplate_sq OWNER TO cadre;

--
-- Name: ad_notificationtemplate_trl; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_notificationtemplate_trl (
                                                   ad_notificationtemplate_id numeric(10,0) NOT NULL,
                                                   ad_client_id numeric(10,0) NOT NULL,
                                                   ad_org_id numeric(10,0) NOT NULL,
                                                   isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                                   created timestamp(6) without time zone DEFAULT now() NOT NULL,
                                                   createdby numeric(10,0) NOT NULL,
                                                   updated timestamp(6) without time zone DEFAULT now() NOT NULL,
                                                   updatedby numeric(10,0) NOT NULL,
                                                   description character varying(255),
                                                   header character varying(255) NOT NULL,
                                                   template text NOT NULL,
                                                   ad_language character varying(6) NOT NULL,
                                                   CONSTRAINT ad_notifitemplate_trl_isactive CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_notificationtemplate_trl OWNER TO cadre;

--
-- Name: ad_oauth2_client; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_oauth2_client (
                                        ad_oauth2_client_id numeric(10,0) NOT NULL,
                                        ad_client_id numeric(10,0) NOT NULL,
                                        ad_org_id numeric(10,0) NOT NULL,
                                        isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        created timestamp without time zone DEFAULT now() NOT NULL,
                                        createdby numeric(10,0) NOT NULL,
                                        updated timestamp without time zone DEFAULT now() NOT NULL,
                                        updatedby numeric(10,0) NOT NULL,
                                        name character varying(60) NOT NULL,
                                        description character varying(255),
                                        clientid character varying(255) NOT NULL,
                                        clientsecret character varying(255) NOT NULL,
                                        islocked character(1) DEFAULT 'N'::bpchar NOT NULL,
                                        dateaccountlocked timestamp without time zone,
                                        ad_oauth2_client_uu character varying(36) DEFAULT NULL::character varying,
                                        isadmin character(1) DEFAULT 'N'::bpchar NOT NULL,
                                        tokenexpiresin numeric(10,0) DEFAULT 0,
                                        ad_user_id numeric(10,0),
                                        isrefreshtokenexpires character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        refreshtokenvalidity numeric(10,0) DEFAULT 0,
                                        ad_app_id numeric(10,0),
                                        CONSTRAINT ad_oauth2_client_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                        CONSTRAINT ad_oauth2_client_isadmin_check CHECK ((isadmin = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                        CONSTRAINT ad_oauth2_client_islocked_check CHECK ((islocked = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_oauth2_client OWNER TO cadre;

--
-- Name: ad_oauth2_client_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_oauth2_client_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_oauth2_client_sq OWNER TO cadre;

--
-- Name: ad_oauth2_client_token; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_oauth2_client_token (
                                              ad_oauth2_client_token_id numeric(10,0) NOT NULL,
                                              ad_client_id numeric(10,0) NOT NULL,
                                              ad_org_id numeric(10,0) NOT NULL,
                                              isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                              created timestamp without time zone DEFAULT now() NOT NULL,
                                              createdby numeric(10,0) NOT NULL,
                                              updated timestamp without time zone DEFAULT now() NOT NULL,
                                              updatedby numeric(10,0) NOT NULL,
                                              accesstoken character varying(255) NOT NULL,
                                              accesstokenexpiration timestamp without time zone,
                                              authorizationcode character varying(255),
                                              authorizationcodeexpiration timestamp without time zone,
                                              isactiveaccesstoken character(1) DEFAULT 'Y'::bpchar,
                                              refreshtoken character varying(255),
                                              refreshtokenexpiration timestamp without time zone,
                                              ad_user_id numeric(10,0) NOT NULL,
                                              ad_oauth2_client_id numeric(10,0) NOT NULL,
                                              ad_oauth2_client_token_uu character varying(36) DEFAULT NULL::character varying,
                                              isactiverefreshtoken character(1) DEFAULT 'N'::bpchar NOT NULL,
                                              ad_app_id numeric(10,0),
                                              CONSTRAINT ad_oauth2_client_token_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                              CONSTRAINT ad_oauth2_client_token_isactiveaccesstoken_check CHECK ((isactiveaccesstoken = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_oauth2_client_token OWNER TO cadre;

--
-- Name: ad_oauth2_client_token_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_oauth2_client_token_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_oauth2_client_token_sq OWNER TO cadre;

--
-- Name: ad_oauth_client_roles; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_oauth_client_roles (
                                             ad_oauth2_client_id numeric(10,0) NOT NULL,
                                             ad_role_id numeric(10,0) NOT NULL,
                                             ad_client_id numeric(10,0) NOT NULL,
                                             ad_org_id numeric(10,0) NOT NULL,
                                             isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                             created timestamp without time zone DEFAULT now() NOT NULL,
                                             createdby numeric(10,0) NOT NULL,
                                             updated timestamp without time zone DEFAULT now() NOT NULL,
                                             updatedby numeric(10,0) NOT NULL,
                                             ad_oauth_client_roles_uu character varying(36) DEFAULT NULL::character varying,
                                             ad_oauth_client_roles_id numeric(10,0) NOT NULL,
                                             CONSTRAINT ad_oauth_client_roles_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_oauth_client_roles OWNER TO cadre;

--
-- Name: ad_oauth_client_roles_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_oauth_client_roles_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_oauth_client_roles_sq OWNER TO cadre;

--
-- Name: ad_object_access; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_object_access (
                                        value character varying(60) NOT NULL,
                                        ad_resource_type_id numeric(10,0) NOT NULL,
                                        ad_client_id numeric(10,0) NOT NULL,
                                        ad_org_id numeric(10,0) NOT NULL,
                                        isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        created timestamp without time zone DEFAULT now() NOT NULL,
                                        createdby numeric(10,0) NOT NULL,
                                        updated timestamp without time zone DEFAULT now() NOT NULL,
                                        updatedby numeric(10,0) NOT NULL,
                                        ad_role_id numeric(10,0) NOT NULL,
                                        ad_object_access_uu character varying(36) DEFAULT NULL::character varying,
                                        isreadonly character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        ad_object_access_id numeric(10,0) NOT NULL,
                                        isexactlymatch character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        CONSTRAINT ad_object_access_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                        CONSTRAINT ad_object_access_isexactlymatch_check CHECK ((isexactlymatch = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                        CONSTRAINT ad_object_access_isreadonly_check CHECK ((isreadonly = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_object_access OWNER TO cadre;

--
-- Name: ad_object_access_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_object_access_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_object_access_sq OWNER TO cadre;

--
-- Name: ad_org; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_org (
                              ad_org_id numeric(10,0) NOT NULL,
                              ad_client_id numeric(10,0) NOT NULL,
                              isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                              created timestamp without time zone DEFAULT now() NOT NULL,
                              createdby numeric(10,0) NOT NULL,
                              updated timestamp without time zone DEFAULT now() NOT NULL,
                              updatedby numeric(10,0) NOT NULL,
                              value character varying(40) NOT NULL,
                              name character varying(60) NOT NULL,
                              description character varying(255),
                              ad_org_uu character varying(36) DEFAULT NULL::character varying,
                              CONSTRAINT ad_org_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_org OWNER TO cadre;

--
-- Name: ad_org_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_org_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_org_sq OWNER TO cadre;

--
-- Name: ad_process; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_process (
                                  ad_process_id numeric(10,0) NOT NULL,
                                  ad_client_id numeric(10,0) NOT NULL,
                                  ad_org_id numeric(10,0) NOT NULL,
                                  isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                  created timestamp(6) without time zone DEFAULT now() NOT NULL,
                                  createdby numeric(10,0) NOT NULL,
                                  updated timestamp(6) without time zone DEFAULT now() NOT NULL,
                                  updatedby numeric(10,0) NOT NULL,
                                  value character varying(40) NOT NULL,
                                  description character varying(255),
                                  help character varying(255),
                                  procedurename character varying(255),
                                  ad_extension_id numeric(10,0),
                                  ad_scripting_id numeric(10,0),
                                  ad_process_uu character varying(36) DEFAULT NULL::character varying,
                                  CONSTRAINT ad_process_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_process OWNER TO cadre;

--
-- Name: ad_process_para; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_process_para (
                                       ad_client_id numeric(10,0) NOT NULL,
                                       ad_process_para_id numeric(10,0) NOT NULL,
                                       ad_process_para_uu character varying(36) DEFAULT NULL::character varying,
                                       ad_org_id numeric(10,0) NOT NULL,
                                       created timestamp(6) without time zone DEFAULT now() NOT NULL,
                                       createdby numeric(10,0) NOT NULL,
                                       defaultvalue character varying(2000),
                                       description character varying(255),
                                       help character varying(2000),
                                       isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                       ismandatory character(1),
                                       issameline character(1) DEFAULT 'N'::bpchar NOT NULL,
                                       label character varying(60) NOT NULL,
                                       placeholder character varying(255) DEFAULT NULL::character varying,
                                       seqno numeric(10,0),
                                       updated timestamp(6) without time zone DEFAULT now() NOT NULL,
                                       updatedby numeric(10,0) NOT NULL,
                                       ad_process_id numeric(10,0) NOT NULL,
                                       bootstrapclass character varying(255) DEFAULT 'p-md-6 p-mb-3'::character varying,
                                       ad_extension_id numeric(10,0) DEFAULT 0 NOT NULL,
                                       dynamicvalidation character varying(255),
                                       columnname character varying(255) NOT NULL,
                                       ad_reference_id numeric(10,0) NOT NULL,
                                       ad_reference_value_id numeric(10,0),
                                       CONSTRAINT ad_process_para_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                       CONSTRAINT ad_process_para_ismandatory_check CHECK ((ismandatory = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                       CONSTRAINT ad_process_para_issameline_check CHECK ((issameline = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_process_para OWNER TO cadre;

--
-- Name: ad_process_para_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_process_para_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_process_para_sq OWNER TO cadre;

--
-- Name: ad_process_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_process_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_process_sq OWNER TO cadre;

--
-- Name: ad_ref_list; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_ref_list (
                                   ad_ref_list_id numeric(10,0) NOT NULL,
                                   ad_client_id numeric(10,0) NOT NULL,
                                   ad_org_id numeric(10,0) NOT NULL,
                                   isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                   created timestamp without time zone DEFAULT now() NOT NULL,
                                   createdby numeric(10,0) NOT NULL,
                                   updated timestamp without time zone DEFAULT now() NOT NULL,
                                   updatedby numeric(10,0) NOT NULL,
                                   value character varying(60) NOT NULL,
                                   name character varying(60) NOT NULL,
                                   description character varying(255),
                                   ad_reference_id numeric(10,0) NOT NULL,
                                   ad_extension_id numeric(10,0) NOT NULL,
                                   ad_ref_list_uu character varying(36) DEFAULT NULL::character varying,
                                   CONSTRAINT ad_ref_list_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_ref_list OWNER TO cadre;

--
-- Name: ad_ref_list_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_ref_list_sq
    START WITH 37
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_ref_list_sq OWNER TO cadre;

--
-- Name: ad_reference; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_reference (
                                    ad_reference_id numeric(10,0) NOT NULL,
                                    ad_client_id numeric(10,0) NOT NULL,
                                    ad_org_id numeric(10,0) NOT NULL,
                                    isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                    created timestamp without time zone DEFAULT now() NOT NULL,
                                    createdby numeric(10,0) NOT NULL,
                                    updated timestamp without time zone DEFAULT now() NOT NULL,
                                    updatedby numeric(10,0) NOT NULL,
                                    name character varying(60) NOT NULL,
                                    description character varying(255),
                                    help character varying(2000),
                                    validationtype character(1),
                                    isorderbyvalue character(1) DEFAULT 'N'::bpchar,
                                    ad_reference_uu character varying(36) DEFAULT NULL::character varying,
                                    ad_extension_id numeric(10,0) DEFAULT 0 NOT NULL,
                                    ad_table_id numeric(10,0),
                                    CONSTRAINT ad_reference_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                    CONSTRAINT ad_reference_isorderbyvalue_check CHECK ((isorderbyvalue = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_reference OWNER TO cadre;

--
-- Name: ad_reference_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_reference_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_reference_sq OWNER TO cadre;

--
-- Name: ad_resource_type; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_resource_type (
                                        ad_resource_type_id numeric(10,0) NOT NULL,
                                        ad_client_id numeric(10,0) NOT NULL,
                                        ad_org_id numeric(10,0) NOT NULL,
                                        isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        created timestamp without time zone DEFAULT now() NOT NULL,
                                        createdby numeric(10,0) NOT NULL,
                                        name character varying(60) NOT NULL,
                                        updated timestamp without time zone DEFAULT now() NOT NULL,
                                        updatedby numeric(10,0) NOT NULL,
                                        ad_resource_type_uu character varying(36) DEFAULT NULL::character varying,
                                        ad_extension_id numeric(10,0) DEFAULT 0 NOT NULL,
                                        CONSTRAINT ad_resource_type_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_resource_type OWNER TO cadre;

--
-- Name: ad_resource_type_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_resource_type_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_resource_type_sq OWNER TO cadre;

--
-- Name: ad_role; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_role (
                               ad_role_id numeric(10,0) NOT NULL,
                               ad_client_id numeric(10,0) NOT NULL,
                               ad_org_id numeric(10,0) NOT NULL,
                               isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                               created timestamp without time zone DEFAULT now() NOT NULL,
                               createdby numeric(10,0) NOT NULL,
                               updated timestamp without time zone DEFAULT now() NOT NULL,
                               name character varying(60) NOT NULL,
                               updatedby numeric(10,0) NOT NULL,
                               description character varying(255),
                               ad_role_uu character varying(36) DEFAULT NULL::character varying,
                               userlevel character varying(3) DEFAULT '  O'::character varying NOT NULL,
                               CONSTRAINT ad_role_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_role OWNER TO cadre;

--
-- Name: ad_role_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_role_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_role_sq OWNER TO cadre;

--
-- Name: ad_scripting; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_scripting (
                                    ad_client_id numeric(10,0) NOT NULL,
                                    ad_org_id numeric(10,0) NOT NULL,
                                    ad_scripting_id numeric(10,0) NOT NULL,
                                    createdby numeric(10,0) NOT NULL,
                                    description character varying(255),
                                    isactive character(1) NOT NULL,
                                    name character varying(60) NOT NULL,
                                    enginetype character varying(1),
                                    content text,
                                    updatedby numeric(10,0) NOT NULL,
                                    value character varying(60) NOT NULL,
                                    ad_scripting_uu character varying(36) DEFAULT NULL::character varying,
                                    created timestamp without time zone DEFAULT now() NOT NULL,
                                    updated timestamp without time zone DEFAULT now() NOT NULL,
                                    CONSTRAINT ad_scripting_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_scripting OWNER TO cadre;

--
-- Name: ad_scripting_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_scripting_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_scripting_sq OWNER TO cadre;

--
-- Name: ad_serviceprovider; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_serviceprovider (
                                          ad_serviceprovider_id numeric(10,0) NOT NULL,
                                          ad_client_id numeric(10,0) NOT NULL,
                                          ad_org_id numeric(10,0) NOT NULL,
                                          isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                          created timestamp without time zone DEFAULT now() NOT NULL,
                                          createdby numeric(10,0) NOT NULL,
                                          updated timestamp without time zone DEFAULT now() NOT NULL,
                                          updatedby numeric(10,0) NOT NULL,
                                          value character varying(60) NOT NULL,
                                          attributes text,
                                          ad_serviceprovider_uu character varying(36) DEFAULT NULL::character varying,
                                          ad_extension_id numeric(10,0) NOT NULL,
                                          classname character varying(255),
                                          servicetype character(1) DEFAULT '1'::bpchar NOT NULL,
                                          CONSTRAINT ad_serviceprovider_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_serviceprovider OWNER TO cadre;

--
-- Name: ad_serviceprovider_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_serviceprovider_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_serviceprovider_sq OWNER TO cadre;

--
-- Name: ad_sysconfig; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_sysconfig (
                                    ad_sysconfig_id numeric(10,0) NOT NULL,
                                    ad_client_id numeric(10,0) NOT NULL,
                                    ad_org_id numeric(10,0) NOT NULL,
                                    isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                    created timestamp without time zone DEFAULT now() NOT NULL,
                                    createdby numeric(10,0) NOT NULL,
                                    updated timestamp without time zone DEFAULT now() NOT NULL,
                                    updatedby numeric(10,0) NOT NULL,
                                    value character varying(255) NOT NULL,
                                    name character varying(255) NOT NULL,
                                    description character varying(255),
                                    ad_sysconfig_uu character varying(36) DEFAULT NULL::character varying,
                                    CONSTRAINT ad_sysconfig_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_sysconfig OWNER TO cadre;

--
-- Name: ad_sysconfig_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_sysconfig_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_sysconfig_sq OWNER TO cadre;

--
-- Name: ad_tab; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_tab (
                              ad_tab_id numeric(10,0) NOT NULL,
                              ad_client_id numeric(10,0) NOT NULL,
                              ad_org_id numeric(10,0) NOT NULL,
                              isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                              created timestamp without time zone DEFAULT now() NOT NULL,
                              createdby numeric(10,0) NOT NULL,
                              updated timestamp without time zone DEFAULT now() NOT NULL,
                              updatedby numeric(10,0) NOT NULL,
                              name character varying(60) NOT NULL,
                              description character varying(255),
                              help character varying(2000),
                              ad_table_id numeric(10,0) NOT NULL,
                              ad_window_id numeric(10,0) NOT NULL,
                              seqno numeric(10,0) NOT NULL,
                              tablevel numeric(10,0) NOT NULL,
                              isreadonly character(1) DEFAULT 'N'::bpchar NOT NULL,
                              isinsertrecord character(1) DEFAULT 'Y'::bpchar NOT NULL,
                              parent_column_id numeric(10,0) DEFAULT NULL::numeric,
                              ad_tab_uu character varying(36) DEFAULT NULL::character varying,
                              ad_extension_id numeric(10,0) DEFAULT 0 NOT NULL,
                              orderbyclause character varying(60),
                              CONSTRAINT ad_tab_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                              CONSTRAINT ad_tab_isinsertrecord_check CHECK ((isinsertrecord = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                              CONSTRAINT ad_tab_isreadonly_check CHECK ((isreadonly = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_tab OWNER TO cadre;

--
-- Name: ad_tab_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_tab_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_tab_sq OWNER TO cadre;

--
-- Name: ad_table; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_table (
                                ad_table_id numeric(10,0) NOT NULL,
                                ad_client_id numeric(10,0) NOT NULL,
                                ad_org_id numeric(10,0) NOT NULL,
                                isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                created timestamp without time zone DEFAULT now() NOT NULL,
                                createdby numeric(10,0) NOT NULL,
                                updated timestamp without time zone DEFAULT now() NOT NULL,
                                updatedby numeric(10,0) NOT NULL,
                                ad_table_uu character varying(36) DEFAULT NULL::character varying,
                                name character varying(60) NOT NULL,
                                description character varying(255),
                                help character varying(2000),
                                tablename character varying(40) NOT NULL,
                                isview character(1) DEFAULT 'N'::bpchar NOT NULL,
                                loadseq numeric(10,0),
                                issecurityenabled character(1) DEFAULT 'N'::bpchar NOT NULL,
                                isdeleteable character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                ishighvolume character(1) DEFAULT 'N'::bpchar NOT NULL,
                                ischangelog character(1) DEFAULT 'N'::bpchar NOT NULL,
                                istranslated character(1) DEFAULT 'N'::bpchar NOT NULL,
                                ad_extension_id numeric(10,0) DEFAULT 0,
                                ispublic character(1) DEFAULT 'N'::bpchar NOT NULL,
                                accesslevel character(1) DEFAULT '4'::bpchar NOT NULL,
                                CONSTRAINT ad_table_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_table_ischangelog_check CHECK ((ischangelog = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_table_isdeleteable_check CHECK ((isdeleteable = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_table_ishighvolume_check CHECK ((ishighvolume = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_table_ispublic_check CHECK ((ispublic = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_table_issecureenabled_check CHECK ((issecurityenabled = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_table_istranslated_check CHECK ((istranslated = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                CONSTRAINT ad_table_isview_check CHECK ((isview = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_table OWNER TO cadre;

--
-- Name: ad_table_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_table_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_table_sq OWNER TO cadre;

--
-- Name: ad_table_trl; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_table_trl (
                                    ad_table_id numeric(10,0) NOT NULL,
                                    ad_language character varying(6) NOT NULL,
                                    name character varying(60) NOT NULL
);


ALTER TABLE cadre.ad_table_trl OWNER TO cadre;

--
-- Name: ad_toolbarbutton; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_toolbarbutton (
                                        ad_toolbarbutton_id numeric(10,0) NOT NULL,
                                        ad_client_id numeric(10,0) NOT NULL,
                                        ad_org_id numeric(10,0) NOT NULL,
                                        isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        created timestamp(6) without time zone DEFAULT now() NOT NULL,
                                        createdby numeric(10,0) NOT NULL,
                                        updated timestamp(6) without time zone DEFAULT now() NOT NULL,
                                        updatedby numeric(10,0) NOT NULL,
                                        name character varying(60) NOT NULL,
                                        icon character varying(255),
                                        description character varying(255),
                                        help character varying(255),
                                        ad_process_id numeric(10,0) NOT NULL,
                                        actionname character varying(60),
                                        ad_tab_id numeric(10,0),
                                        islinkedtoselectedrecord character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        ad_toolbarbutton_uu character varying(36) DEFAULT NULL::character varying,
                                        CONSTRAINT ad_toolbarbutton_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_toolbarbutton OWNER TO cadre;

--
-- Name: ad_toolbarbutton_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_toolbarbutton_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_toolbarbutton_sq OWNER TO cadre;

--
-- Name: ad_tree; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_tree (
                               ad_tree_id numeric(10,0) NOT NULL,
                               ad_client_id numeric(10,0) NOT NULL,
                               ad_org_id numeric(10,0) NOT NULL,
                               created timestamp without time zone DEFAULT now() NOT NULL,
                               createdby numeric(10,0) NOT NULL,
                               updated timestamp without time zone DEFAULT now() NOT NULL,
                               updatedby numeric(10,0) NOT NULL,
                               isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                               name character varying(60) NOT NULL,
                               description character varying(255),
                               isdefault character(1) DEFAULT 'N'::bpchar NOT NULL,
                               ad_tree_uu character varying(36) DEFAULT NULL::character varying,
                               CONSTRAINT ad_tree_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                               CONSTRAINT ad_tree_isdefault_check CHECK ((isdefault = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_tree OWNER TO cadre;

--
-- Name: ad_tree_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_tree_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_tree_sq OWNER TO cadre;

--
-- Name: ad_treenode; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_treenode (
                                   ad_tree_id numeric(10,0) NOT NULL,
                                   ad_treenode_id numeric(10,0) NOT NULL,
                                   ad_client_id numeric(10,0) NOT NULL,
                                   ad_org_id numeric(10,0) NOT NULL,
                                   isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                   created timestamp without time zone DEFAULT now() NOT NULL,
                                   createdby numeric(10,0) NOT NULL,
                                   updated timestamp without time zone DEFAULT now() NOT NULL,
                                   updatedby numeric(10,0) NOT NULL,
                                   ad_treenode_parent_id numeric(10,0),
                                   name character varying(60) NOT NULL,
                                   issummary character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                   seqno numeric(10,0),
                                   ad_treenode_uu character varying(36) DEFAULT NULL::character varying,
                                   ad_window_id numeric(10,0),
                                   CONSTRAINT ad_treenode_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                                   CONSTRAINT ad_treenode_issummary_check CHECK ((issummary = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_treenode OWNER TO cadre;

--
-- Name: ad_treenode_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_treenode_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_treenode_sq OWNER TO cadre;

--
-- Name: ad_user; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_user (
                               ad_user_id numeric(10,0) NOT NULL,
                               ad_client_id numeric(10,0) NOT NULL,
                               ad_org_id numeric(10,0) NOT NULL,
                               isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                               created timestamp without time zone DEFAULT now() NOT NULL,
                               createdby numeric(10,0) NOT NULL,
                               updated timestamp without time zone DEFAULT now() NOT NULL,
                               updatedby numeric(10,0) NOT NULL,
                               emailuser character varying(60) NOT NULL,
                               userpin character varying(60),
                               ad_user_uu character varying(36) DEFAULT NULL::character varying,
                               islocked character(1) DEFAULT 'N'::bpchar NOT NULL,
                               dateaccountlocked timestamp without time zone,
                               datelastlogin timestamp without time zone,
                               isaccountverified character(1) DEFAULT 'N'::bpchar NOT NULL,
                               name character varying(60) NOT NULL,
                               userlevel character varying(3) DEFAULT '  O'::character varying NOT NULL,
                               isadmin character(1) DEFAULT 'N'::bpchar NOT NULL,
                               isviewonlyactiverecords character(1) DEFAULT 'Y'::bpchar,
                               CONSTRAINT ad_user_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))),
                               CONSTRAINT ad_user_islocked_check CHECK ((islocked = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_user OWNER TO cadre;

--
-- Name: ad_user_app; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_user_app (
                                   ad_user_id numeric(10,0) NOT NULL,
                                   ad_user_app_id numeric(10,0) NOT NULL,
                                   ad_client_id numeric(10,0) NOT NULL,
                                   ad_org_id numeric(10,0) NOT NULL,
                                   isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                   created timestamp without time zone DEFAULT now() NOT NULL,
                                   createdby numeric(10,0) NOT NULL,
                                   updated timestamp without time zone DEFAULT now() NOT NULL,
                                   updatedby numeric(10,0) NOT NULL,
                                   ad_user_app_uu character varying(36) DEFAULT NULL::character varying,
                                   ad_app_id numeric(10,0),
                                   CONSTRAINT ad_user_app_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_user_app OWNER TO cadre;

--
-- Name: ad_user_app_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_user_app_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_user_app_sq OWNER TO cadre;

--
-- Name: ad_user_roles; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_user_roles (
                                     ad_user_id numeric(10,0) NOT NULL,
                                     ad_role_id numeric(10,0) NOT NULL,
                                     ad_client_id numeric(10,0) NOT NULL,
                                     ad_org_id numeric(10,0) NOT NULL,
                                     isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                     created timestamp without time zone DEFAULT now() NOT NULL,
                                     createdby numeric(10,0) NOT NULL,
                                     updated timestamp without time zone DEFAULT now() NOT NULL,
                                     updatedby numeric(10,0) NOT NULL,
                                     ad_user_roles_uu character varying(36) DEFAULT NULL::character varying,
                                     ad_user_roles_id numeric(10,0) NOT NULL,
                                     CONSTRAINT ad_user_roles_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_user_roles OWNER TO cadre;

--
-- Name: ad_user_roles_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_user_roles_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_user_roles_sq OWNER TO cadre;

--
-- Name: ad_user_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_user_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_user_sq OWNER TO cadre;

--
-- Name: ad_variable; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_variable (
                                   ad_variable_id numeric(10,0) NOT NULL,
                                   ad_client_id numeric(10,0) NOT NULL,
                                   ad_org_id numeric(10,0) NOT NULL,
                                   isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                   created timestamp without time zone DEFAULT now() NOT NULL,
                                   createdby numeric(10,0) NOT NULL,
                                   updated timestamp without time zone DEFAULT now() NOT NULL,
                                   updatedby numeric(10,0) NOT NULL,
                                   ad_reference_id numeric(10,0) NOT NULL,
                                   classname character varying(255),
                                   columnsql character varying(255),
                                   constantvalue character varying(255),
                                   description character varying(255),
                                   type character(1) NOT NULL,
                                   value character varying(255) NOT NULL,
                                   ad_variable_uu character varying(36) DEFAULT NULL::character varying,
                                   CONSTRAINT ad_variable_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_variable OWNER TO cadre;

--
-- Name: ad_variable_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_variable_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_variable_sq OWNER TO cadre;

--
-- Name: ad_window; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_window (
                                 ad_window_id numeric(10,0) NOT NULL,
                                 ad_client_id numeric(10,0) NOT NULL,
                                 ad_org_id numeric(10,0) NOT NULL,
                                 isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                 created timestamp without time zone DEFAULT now() NOT NULL,
                                 createdby numeric(10,0) NOT NULL,
                                 updated timestamp without time zone DEFAULT now() NOT NULL,
                                 updatedby numeric(10,0) NOT NULL,
                                 name character varying(60) NOT NULL,
                                 description character varying(255),
                                 help character varying(2000),
                                 ad_window_uu character varying(36) DEFAULT NULL::character varying,
                                 ad_extension_id numeric(10,0) DEFAULT 0 NOT NULL,
                                 CONSTRAINT ad_window_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_window OWNER TO cadre;

--
-- Name: ad_window_access; Type: TABLE; Schema: cadre; Owner: cadre
--

CREATE TABLE cadre.ad_window_access (
                                        ad_window_access_id numeric(10,0) NOT NULL,
                                        ad_client_id numeric(10,0) NOT NULL,
                                        ad_org_id numeric(10,0) NOT NULL,
                                        isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        created timestamp without time zone DEFAULT now() NOT NULL,
                                        createdby numeric(10,0) NOT NULL,
                                        updated timestamp without time zone DEFAULT now() NOT NULL,
                                        updatedby numeric(10,0) NOT NULL,
                                        ad_role_id numeric(10,0) NOT NULL,
                                        ad_window_id numeric(10,0) NOT NULL,
                                        readonly character(1) DEFAULT 'Y'::bpchar NOT NULL,
                                        ad_window_access_uu character varying(36) DEFAULT NULL::character varying,
                                        CONSTRAINT ad_window_access_isactive_check CHECK ((isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cadre.ad_window_access OWNER TO cadre;

--
-- Name: ad_window_access_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_window_access_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cadre.ad_window_access_sq OWNER TO cadre;

--
-- Name: ad_window_sq; Type: SEQUENCE; Schema: cadre; Owner: cadre
--

CREATE SEQUENCE cadre.ad_window_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE cadre.ad_window_sq OWNER TO cadre;

--
-- Data for Name: ad_app; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_app (ad_app_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, value, description, ad_app_uu) FROM stdin;
1	0	0	Y	2021-02-06 15:36:24.385968	0	2021-02-06 15:36:24.385968	0	ng-cadre-app	\N	faa32fc6-fec3-43d5-94ba-2aae6a5e1b8e
\.


--
-- Data for Name: ad_apprule; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_apprule (ad_apprule_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_table_id, expression, ad_apprule_uu, ad_app_id, ad_role_id) FROM stdin;
\.


--
-- Data for Name: ad_attachment; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_attachment (ad_attachment_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_media_id, ad_table_id, ad_record_id, ad_attachment_uu) FROM stdin;
\.


--
-- Data for Name: ad_client; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_client (ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, value, name, description, ad_language, ad_client_uu, ad_tree_id) FROM stdin;
0	0	Y	2019-12-31 14:52:02.867049	0	2021-03-12 20:33:52.541	0	Cadre	Cadre	Cadre Platform	en_US	11237b53-9592-4af1-b3c5-afd216514b5d	0
\.


--
-- Data for Name: ad_column; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_column (ad_client_id, ad_column_id, ad_column_uu, ad_org_id, ad_reference_id, ad_table_id, columnname, created, createdby, description, help, isactive, iskey, ismandatory, name, updated, updatedby, istranslatable, ad_extension_id, referencevalue, ad_reference_value_id, updatable, isidentifier) FROM stdin;
0	374	\N	0	10	2	AD_Oauth2_Client_UU	2020-03-14 13:01:29.328121	0			Y	N	N	AD_Oauth2_Client_UU	2020-03-14 13:01:29.328121	0	N	0	\N	\N	Y	N
0	393	\N	0	10	9	DynamicValidation	2020-03-24 19:48:53.355595	0			Y	N	N	Dynamic Validation	2020-03-24 19:48:53.355595	0	N	0	\N	\N	Y	N
0	438	\N	0	19	36	AD_OAuth2_Client_ID	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	Y	AD_OAuth2_Client_ID	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	725	\N	0	19	55	AD_Process_ID	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	AD_Process_ID	2021-05-06 20:43:00.483	0	N	0	\N	\N	Y	N
0	530	\N	0	10	41	Name	2020-04-16 11:28:41.42086	0	\N	\N	Y	N	N	Name	2021-03-15 09:28:07.621	0	N	0	\N	\N	Y	S
0	1	\N	0	13	1	AD_Resource_Type_ID	2020-01-22 20:38:37.144048	0			Y	Y	N	AD_Resource_Type_ID	2020-01-22 20:38:37.144048	0	N	0	\N	\N	Y	N
0	448	\N	0	13	36	AD_OAuth_Client_Roles_ID	2020-03-27 19:51:53.250578	0	\N	\N	Y	Y	Y	AD_OAuth_Client_Roles_ID	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	344	\N	0	19	31	AD_Client_ID	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	AD_Client_ID	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	345	\N	0	19	31	AD_Org_ID	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	AD_Org_ID	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	346	\N	0	20	31	IsActive	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	IsActive	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	347	\N	0	16	31	Created	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	Created	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	349	\N	0	16	31	Updated	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	Updated	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	350	\N	0	18	31	UpdatedBy	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	UpdatedBy	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	348	\N	0	18	31	CreatedBy	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	CreatedBy	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	351	\N	0	19	31	AD_Media_ID	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	AD_Media_ID	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	352	\N	0	19	31	AD_Table_ID	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	AD_Table_ID	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	353	\N	0	11	31	AD_Record_ID	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	Y	AD_Record_ID	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	354	\N	0	10	31	AD_Attachment_UU	2020-03-07 20:38:31.418033	0	\N	\N	Y	N	N	AD_Attachment_UU	2020-03-07 20:38:31.418033	0	N	0	\N	\N	Y	N
0	447	\N	0	10	36	AD_OAuth_Client_Roles_UU	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	N	AD_OAuth_Client_Roles_UU	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	4	\N	0	20	1	IsActive	2020-01-22 20:39:54.945177	0			Y	N	N	Active	2020-01-22 20:39:54.945177	0	N	0	\N	\N	Y	N
0	176	7ee3ce36-b7eb-4dbd-a8a2-8065295c57dd	0	16	15	Created	2019-12-31 15:43:11.969246	0	Date this record was created	\N	Y	N	Y	Created	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	177	29ec9608-e75d-4048-bf05-fb204b6d5a2c	0	19	15	CreatedBy	2019-12-31 15:43:11.969246	0	User who created this records	\N	Y	N	Y	Created By	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	179	166a6575-474d-4ec3-b059-5bf42bda064d	0	19	15	UpdatedBy	2019-12-31 15:43:11.969246	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	183	d1c9a22e-f230-4d58-ab6a-c441b8fd887f	0	11	15	SeqNo	2019-12-31 15:43:11.969246	0	Method of ordering records; lowest number comes first	\N	Y	N	Y	Sequence	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	210	f73d6d74-0225-4fe2-a045-7e26af7df100	0	10	15	AD_TreeNode_UU	2019-12-31 15:43:11.969246	100		\N	Y	N	N	AD_TreeNode_UU	2019-12-31 15:43:11.969246	100	N	0	\N	\N	Y	N
0	2	\N	0	19	1	AD_Client_ID	2020-01-22 20:39:00.385368	0			Y	N	N	Client	2020-01-22 20:39:00.385368	0	N	0	\N	\N	Y	N
0	184	462d0137-019d-46d1-8a4a-50aedac46b74	0	20	4	IsHighVolume	2019-12-28 17:29:13.29521	0	Use Search instead of Pick list	The High Volume Checkbox indicates if a search screen will display as opposed to a pick list for selecting records from this table.	Y	N	Y	High Volume	2019-12-28 17:29:13.29521	0	N	0	\N	\N	Y	N
0	186	085ea5ce-0f45-4771-9b97-78de7e7464b9	0	20	4	IsView	2019-12-28 17:29:23.910612	0	This is a view	This is a view rather than a table.  A view is always treated as read only in the system.	Y	N	Y	View	2019-12-28 17:29:23.910612	0	N	0	\N	\N	Y	N
0	199	4186efab-1337-454e-b60e-8ed48057d5de	0	20	4	IsChangeLog	2019-12-28 17:53:11.109694	0	Maintain a log of changes	\N	Y	N	Y	Maintain Change Log	2019-12-28 17:53:11.109694	100	N	0	\N	\N	Y	N
0	209	c6999b66-f86a-4e7a-b3c6-1f2c42db8af6	0	10	6	AD_Reference_UU	2020-01-11 14:52:03.447138	100		\N	Y	N	N	AD_Reference_UU	2020-01-11 14:52:03.447138	100	N	0	\N	\N	Y	N
0	219	\N	0	20	5	IsMandatory	2020-01-30 07:56:35.742493	0			Y	N	N	IsMandatory	2020-01-30 07:56:35.742493	0	N	0	\N	\N	Y	N
0	343	\N	0	13	31	AD_Attachment_ID	2020-03-07 20:38:31.418033	0	\N	\N	Y	Y	Y	AD_Attachment_ID	2021-03-15 09:14:07.627	0	N	0	\N	\N	Y	Y
0	440	\N	0	19	36	AD_Client_ID	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	Y	AD_Client_ID	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	441	\N	0	19	36	AD_Org_ID	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	Y	AD_Org_ID	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	439	\N	0	19	36	AD_Role_ID	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	Y	AD_Role_ID	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	443	\N	0	16	36	Created	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	Y	Created	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	444	\N	0	18	36	CreatedBy	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	Y	CreatedBy	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	442	\N	0	20	36	IsActive	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	Y	IsActive	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	445	\N	0	16	36	Updated	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	Y	Updated	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	446	\N	0	18	36	UpdatedBy	2020-03-27 19:51:53.250578	0	\N	\N	Y	N	Y	UpdatedBy	2020-03-27 19:51:53.250578	0	N	0	\N	\N	Y	N
0	500	\N	0	13	40	AD_NotificationTemplate_ID	2020-04-16 08:56:34.354575	0	\N	\N	Y	Y	Y	AD_NotificationTemplate_ID	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	502	\N	0	19	40	AD_Org_ID	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	AD_Org_ID	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	504	\N	0	16	40	Created	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	Created	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	505	\N	0	18	40	CreatedBy	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	CreatedBy	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	503	\N	0	20	40	IsActive	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	IsActive	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	216	cd639a6d-243f-4737-80b7-e953534cafa6	0	19	15	AD_Window_ID	2019-12-31 19:13:25.918113	100	Window	\N	Y	N	N	AD_Window_ID	2019-12-31 19:13:25.918113	0	N	0	\N	\N	Y	N
0	180	13e050a0-cc39-432c-aa4b-f22c8d5661e4	0	19	15	AD_Tree_ID	2019-12-31 15:43:11.969246	0	Identifies a Tree	\N	Y	N	Y	AD_Tree_ID	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	181	058aa3ec-048b-46f9-8614-1221ab590d7a	0	13	15	AD_TreeNode_ID	2019-12-31 15:43:11.969246	0		\N	Y	Y	Y	AD_TreeNode_ID	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	182	d8f5020d-c7f5-4f88-b9c9-c6f47a92aa3b	0	18	15	AD_TreeNode_Parent_ID	2019-12-31 15:43:11.969246	0	Parent of Entity	\N	Y	N	N	AD_TreeNode_Parent_ID	2019-12-31 15:43:11.969246	0	N	0	\N	44	Y	N
0	267	\N	0	10	24	Help	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	N	Help	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	284	\N	0	10	26	ServiceProviderClass	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	N	Service Provider Class Name	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	42	9eef2293-6c6b-40b6-8821-e73637a46798	0	10	4	Name	2019-12-28 17:53:10.866213	0	Alphanumeric identifier of the entity	\N	Y	N	Y	Name	2019-12-28 17:53:10.866213	100	N	0	\N	\N	Y	S
0	265	\N	0	10	24	Name	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	Y	Name	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	S
0	282	\N	0	10	26	Name	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	Y	Name	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	S
0	733	\N	0	19	56	AD_Org_ID	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	AD_Org_ID	2021-05-08 20:08:46.602	0	N	0	\N	\N	Y	N
0	731	\N	0	13	56	AD_Process_Para_ID	2021-05-08 20:06:04.603468	0	\N	\N	Y	Y	Y	AD_Process_Para_ID	2021-05-08 20:09:02.136	0	N	0	\N	\N	Y	N
0	732	\N	0	10	56	AD_Process_Para_UU	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	N	AD_Process_Para_UU	2021-05-08 20:09:10.141	0	N	0	\N	\N	Y	N
0	356	\N	0	19	30	AD_Client_ID	2020-03-09 21:52:15.563592	0	\N	\N	Y	N	Y	AD_Client_ID	2020-03-09 21:52:15.563592	0	N	0	\N	\N	Y	N
0	357	\N	0	19	30	AD_Org_ID	2020-03-09 21:52:15.563592	0	\N	\N	Y	N	Y	AD_Org_ID	2020-03-09 21:52:15.563592	0	N	0	\N	\N	Y	N
0	358	\N	0	20	30	IsActive	2020-03-09 21:52:15.563592	0	\N	\N	Y	N	Y	IsActive	2020-03-09 21:52:15.563592	0	N	0	\N	\N	Y	N
0	359	\N	0	16	30	Created	2020-03-09 21:52:15.563592	0	\N	\N	Y	N	Y	Created	2020-03-09 21:52:15.563592	0	N	0	\N	\N	Y	N
0	360	\N	0	18	30	CreatedBy	2020-03-09 21:52:15.563592	0	\N	\N	Y	N	Y	CreatedBy	2020-03-09 21:52:15.563592	0	N	0	\N	\N	Y	N
0	361	\N	0	16	30	Updated	2020-03-09 21:52:15.563592	0	\N	\N	Y	N	Y	Updated	2020-03-09 21:52:15.563592	0	N	0	\N	\N	Y	N
0	362	\N	0	18	30	UpdatedBy	2020-03-09 21:52:15.563592	0	\N	\N	Y	N	Y	UpdatedBy	2020-03-09 21:52:15.563592	0	N	0	\N	\N	Y	N
0	227	\N	0	19	21	AD_Org_ID	2020-01-30 07:57:56.157636	0	\N	\N	Y	N	Y	AD_Org_ID	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	225	\N	0	19	21	AD_Client_ID	2020-01-30 07:57:56.157636	0	\N	\N	Y	N	Y	AD_Client_ID	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	223	\N	0	19	21	AD_Resource_Type_ID	2020-01-30 07:57:56.157636	0		\N	Y	N	Y	AD_Resource_Type_ID	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	239	\N	0	19	21	AD_Role_ID	2020-01-30 07:57:56.157636	0	\N	\N	Y	N	Y	AD_Role_ID	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	272	\N	0	19	24	AD_Table_ID	2020-02-05 21:28:35.635128	0			Y	N	N	AD_Table_ID	2020-02-05 21:28:35.635128	0	N	0	\N	\N	Y	N
0	235	\N	0	16	21	Updated	2020-01-30 07:57:56.157636	0	\N	\N	Y	N	Y	Updated	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	233	\N	0	18	21	CreatedBy	2020-01-30 07:57:56.157636	0	\N	\N	Y	N	Y	CreatedBy	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	237	\N	0	18	21	UpdatedBy	2020-01-30 07:57:56.157636	0	\N	\N	Y	N	Y	UpdatedBy	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	229	\N	0	20	21	IsActive	2020-01-30 07:57:56.157636	0	\N	\N	Y	N	Y	IsActive	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	231	\N	0	16	21	Created	2020-01-30 07:57:56.157636	0	\N	\N	Y	N	Y	Created	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	257	\N	0	19	24	AD_Client_ID	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	Y	AD_Client_ID	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	259	\N	0	19	24	AD_Org_ID	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	Y	Organization	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	260	\N	0	16	24	Created	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	Y	Created	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	261	\N	0	18	24	CreatedBy	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	Y	CreatedBy	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	262	\N	0	16	24	Updated	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	Y	Updated	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	263	\N	0	18	24	UpdatedBy	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	Y	UpdatedBy	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	264	\N	0	20	24	IsActive	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	Y	IsActive	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	266	\N	0	10	24	Description	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	N	Description	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	269	\N	0	10	24	ModelValidationClass	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	Y	ModelValidationClass	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	270	\N	0	11	24	SeqNo	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	N	SeqNo	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	271	\N	0	10	24	AD_ModelValidator_UU	2020-02-05 21:19:10.158173	0	\N	\N	Y	N	N	AD_ModelValidator_UU	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	273	\N	0	20	2	IsAdmin	2020-02-22 23:05:11.243051	0			Y	N	N	Admin	2020-02-22 23:05:11.243051	0	N	0	\N	\N	Y	N
0	275	\N	0	19	26	AD_Client_ID	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	Y	AD_Client_ID	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	276	\N	0	19	26	AD_Org_ID	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	Y	AD_Org_ID	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	277	\N	0	20	26	IsActive	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	Y	IsActive	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	278	\N	0	16	26	Created	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	Y	Created	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	279	\N	0	18	26	CreatedBy	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	Y	CreatedBy	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	280	\N	0	16	26	Updated	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	Y	Updated	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	281	\N	0	18	26	UpdatedBy	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	Y	UpdatedBy	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	283	\N	0	10	26	Description	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	N	Description	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	376	\N	0	19	32	AD_Client_ID	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	AD_Client_ID	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	392	\N	0	10	32	AD_Language_UU	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	N	AD_Language_UU	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	377	\N	0	19	32	AD_Org_ID	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	AD_Org_ID	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	385	\N	0	10	32	CountryCode	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	N	CountryCode	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	379	\N	0	16	32	Created	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	Created	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	380	\N	0	18	32	CreatedBy	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	CreatedBy	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	390	\N	0	10	32	DatePattern	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	N	DatePattern	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	378	\N	0	20	32	IsActive	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	IsActive	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	386	\N	0	20	32	IsBaseLanguage	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	IsBaseLanguage	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	389	\N	0	20	32	IsDecimalPoint	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	N	IsDecimalPoint	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	387	\N	0	20	32	IsSystemLanguage	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	IsSystemLanguage	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	384	\N	0	10	32	LanguageISO	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	N	LanguageISO	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	391	\N	0	10	32	TimePattern	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	N	TimePattern	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	381	\N	0	16	32	Updated	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	Updated	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	382	\N	0	18	32	UpdatedBy	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	UpdatedBy	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	388	\N	0	13	32	AD_Language_ID	2020-03-14 13:39:59.177351	0	\N	\N	Y	Y	Y	AD_Language_ID	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	N
0	394	\N	0	10	8	OrderByClause	2020-03-25 18:09:03.008033	0			Y	N	N	OrderBy Clause	2020-03-25 18:09:03.008033	0	N	0	\N	\N	Y	N
0	290	\N	0	11	26	SeqNo	2020-02-26 23:27:28.288669	0			Y	N	N	SeqNo	2020-02-26 23:27:28.288669	0	N	0	\N	\N	Y	N
0	258	\N	0	13	24	AD_ModelValidator_ID	2020-02-05 21:19:10.158173	0	\N	\N	Y	Y	Y	AD_ModelValidator_ID	2020-02-05 21:19:10.158173	0	N	0	\N	\N	Y	N
0	85	32ced402-10e8-4a0f-af0a-5618b8309494	0	19	8	AD_Table_ID	2019-12-28 17:53:10.953313	0	Database Table information	\N	Y	N	Y	Table	2019-12-28 17:53:10.953313	0	N	0	\N	\N	Y	N
0	108	8c9a2e3f-33fb-48ee-80b9-51e72c2340b4	0	16	4	Created	2019-12-28 17:53:10.985333	0	Date this record was created	\N	Y	N	Y	Created	2019-12-28 17:53:10.985333	0	N	0	\N	\N	Y	N
0	110	7397de35-2fbe-4610-a8a5-a7b362ca06cc	0	16	4	Updated	2019-12-28 17:53:10.990607	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-28 17:53:10.990607	0	N	0	\N	\N	Y	N
0	111	55260c83-96eb-41e8-b170-1b452bf46507	0	19	4	UpdatedBy	2019-12-28 17:53:10.99314	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-28 17:53:10.99314	0	N	0	\N	\N	Y	N
0	157	dccaef82-8886-43d9-8e64-d270b92152e3	0	20	4	IsSecurityEnabled	2019-12-28 17:25:50.381737	0	If security is enabled, user access to data can be restricted via Roles	The Security Enabled checkbox indicates that user access to the data in this table can be restricted using Roles.	Y	N	Y	Security enabled	2019-12-28 17:25:50.381737	0	N	0	\N	\N	Y	N
0	158	f1ca5cc2-bffd-4cee-9719-72053201863d	0	20	4	IsDeleteable	2019-12-28 17:53:11.0825	0	Indicates if records can be deleted from the database	\N	Y	N	Y	Records deletable	2019-12-28 17:53:11.0825	100	N	0	\N	\N	Y	N
0	109	d8c65c88-2607-40c7-a361-0c0325343b2a	0	18	4	CreatedBy	2019-12-28 17:53:10.987983	0	User who created this records	\N	Y	N	Y	Created By	2021-05-06 21:35:44.528	0	N	0	\N	49	Y	N
0	48	521be54f-6b53-4ba6-872a-d51e0715bdc3	0	10	5	Description	2019-12-28 17:53:10.896391	0	Optional short description of the record	\N	Y	N	N	Description	2019-12-28 17:53:10.896391	0	N	0	\N	\N	Y	N
0	50	1f657e72-1df9-4aa6-8362-b26fac341bf2	0	19	5	AD_Table_ID	2019-12-28 17:53:10.905103	0	Database Table information	\N	Y	N	Y	Table	2019-12-28 17:53:10.905103	0	N	0	\N	\N	Y	N
0	51	67bb6666-3a73-4886-ac95-98d069bff819	0	10	5	ColumnName	2019-12-28 17:53:10.909263	0	Name of the column in the database	\N	Y	N	Y	DB Column Name	2019-12-28 17:53:10.909263	0	N	0	\N	\N	Y	N
0	52	36995004-b533-42a0-ac74-b35f218c36c5	0	20	5	IsKey	2019-12-28 17:29:02.202779	0	This column is the key in this table	The key column must also be display sequence 0 in the field definition and may be hidden.	Y	N	Y	Key column	2019-12-28 17:29:02.202779	0	N	0	\N	\N	Y	N
0	49	ef7a7459-0b8a-429a-b2ab-37156f35d2d8	0	10	5	Help	2019-12-28 17:53:10.900593	0	Comment or Hint	\N	Y	N	N	Comment/Help	2019-12-28 17:53:10.900593	0	N	0	\N	\N	Y	N
0	88	25548654-6955-4498-8dcd-024a66d28e8f	0	19	5	AD_Client_ID	2019-12-28 17:53:10.961772	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2019-12-28 17:53:10.961772	0	N	0	\N	\N	Y	N
0	89	f43390f1-bb45-4f41-bfa1-0cd0a5b6ea87	0	19	5	AD_Org_ID	2019-12-28 17:53:10.964477	0	Organizational entity within client	\N	Y	N	Y	Organization	2019-12-28 17:53:10.964477	0	N	0	\N	\N	Y	N
0	22	\N	0	20	2	IsLocked	2020-01-24 13:58:24.239018	0			Y	N	N	IsLocked	2020-01-24 13:58:24.239018	0	N	0	\N	\N	Y	N
0	23	\N	0	16	2	DateAccountLocked	2020-01-24 13:58:46.44033	0			Y	N	N	DateAccountLocked	2020-01-24 13:58:46.44033	0	N	0	\N	\N	Y	N
0	25	\N	0	18	3	AD_Client_ID	2020-01-27 09:19:56.835399	0			Y	N	N	AD_Client_ID	2020-01-27 09:19:56.835399	0	N	0	\N	\N	Y	N
0	27	\N	0	20	3	IsActive	2020-01-27 09:20:32.824682	0			Y	N	N	Active	2020-01-27 09:20:32.824682	0	N	0	\N	\N	Y	N
0	28	\N	0	16	3	Created	2020-01-27 09:20:48.37362	0			Y	N	N	Created	2020-01-27 09:20:48.37362	0	N	0	\N	\N	Y	N
0	29	\N	0	19	3	CreatedBy	2020-01-27 09:21:10.071033	0			Y	N	N	CreatedBy	2020-01-27 09:21:10.071033	0	N	0	\N	\N	Y	N
0	30	\N	0	16	3	Updated	2020-01-27 09:21:36.194301	0			Y	N	N	Updated	2020-01-27 09:21:36.194301	0	N	0	\N	\N	Y	N
0	31	\N	0	18	3	UpdatedBy	2020-01-27 09:22:03.966436	0			Y	N	N	UpdatedBy	2020-01-27 09:22:03.966436	0	N	0	\N	\N	Y	N
0	32	\N	0	10	3	AccessToken	2020-01-27 09:22:23.778011	0			Y	N	N	Access Token	2020-01-27 09:22:23.778011	0	N	0	\N	\N	Y	N
0	33	\N	0	16	3	AccessTokenExpiration	2020-01-27 09:22:47.320164	0			Y	N	N	Access Token Expiration	2020-01-27 09:22:47.320164	0	N	0	\N	\N	Y	N
0	34	\N	0	10	3	AuthorizationCode	2020-01-27 09:23:05.635525	0			Y	N	N	Authorization Code	2020-01-27 09:23:05.635525	0	N	0	\N	\N	Y	N
0	112	8819a658-3929-461f-8404-1a9b946afee7	0	20	5	IsActive	2019-12-28 17:53:10.995906	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-28 17:53:10.995906	0	N	0	\N	\N	Y	N
0	113	c9df4ef8-9777-4659-bc4e-e4ab02aff2a8	0	16	5	Created	2019-12-28 17:53:10.999074	0	Date this record was created	\N	Y	N	Y	Created	2019-12-28 17:53:10.999074	0	N	0	\N	\N	Y	N
0	114	f6b5777b-5be9-4f1c-bfb2-664ac47e3345	0	19	5	CreatedBy	2019-12-28 17:53:11.001712	0	User who created this records	\N	Y	N	Y	Created By	2019-12-28 17:53:11.001712	0	N	0	\N	\N	Y	N
0	115	20820970-e239-4b6d-8a4b-fbd920c7530c	0	16	5	Updated	2019-12-28 17:53:11.007792	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-28 17:53:11.007792	0	N	0	\N	\N	Y	N
0	116	90dddb41-8072-47c7-a7af-9bb701b18bc2	0	19	5	UpdatedBy	2019-12-28 17:53:11.014326	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-28 17:53:11.014326	0	N	0	\N	\N	Y	N
0	56	f301533e-ecbc-4d17-bcb1-6b092b56e784	0	10	6	Help	2020-01-11 14:51:33.430127	0	Comment or Hint	\N	Y	N	N	Comment/Help	2020-01-11 14:51:33.430127	0	N	0	\N	\N	Y	N
0	55	fe39be59-7d3f-4d21-806c-c7314af4efd2	0	10	6	Description	2020-01-11 14:51:33.426477	0	Optional short description of the record	\N	Y	N	N	Description	2020-01-11 14:51:33.426477	0	N	0	\N	\N	Y	N
0	90	5a30dc69-d1ab-4ae6-9431-a5b186b5631c	0	19	6	AD_Client_ID	2020-01-11 14:51:33.436712	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2020-01-11 14:51:33.436712	0	N	0	\N	\N	Y	N
0	91	59ef7239-1c65-4185-be11-38096f0a57bf	0	19	6	AD_Org_ID	2020-01-11 14:51:33.439955	0	Organizational entity within client	\N	Y	N	Y	Organization	2020-01-11 14:51:33.439955	0	N	0	\N	\N	Y	N
0	117	5818fa47-6ec0-4874-8b2d-d54c63002c50	0	20	6	IsActive	2020-01-11 14:51:33.442943	0	The record is active in the system	\N	Y	N	Y	Active	2020-01-11 14:51:33.442943	0	N	0	\N	\N	Y	N
0	118	90acac41-a62c-4d61-95ef-6dbda943f9f1	0	16	6	Created	2020-01-11 14:51:33.445746	0	Date this record was created	\N	Y	N	Y	Created	2020-01-11 14:51:33.445746	0	N	0	\N	\N	Y	N
0	21	\N	0	10	2	ClientSecret	2020-01-24 13:58:11.991597	0			Y	N	N	ClientSecret	2020-01-24 13:58:11.991597	0	N	0	\N	\N	Y	N
0	57	8610b342-21c1-48d7-8fc0-b5d085662a78	0	17	6	ValidationType	2020-01-11 14:51:33.43352	0	Different method of validating data	\N	Y	N	Y	Validation type	2020-01-11 14:51:33.43352	0	N	0	\N	1	Y	N
0	395	\N	0	13	33	AD_Ref_List_ID	2020-03-27 09:43:25.843312	0	\N	\N	Y	Y	Y	AD_Ref_List_ID	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	24	\N	0	13	3	AD_OAuth2_Client_Token_ID	2020-01-27 09:19:34.113434	0			Y	Y	N	AD_OAuth2_Client_Token_ID	2020-01-27 09:19:34.113434	0	N	0	\N	\N	Y	N
0	532	\N	0	19	11	AD_Tree_ID	2020-04-19 13:10:03.532372	0	\N	\N	Y	N	Y	AD_Tree_ID	2021-03-12 20:36:51.309	0	N	0	\N	\N	Y	N
0	46	ea4f7579-243d-42a0-a684-3e32d07c55c2	0	13	5	AD_Column_ID	2019-12-28 17:26:21.938396	0	Column in the table	Link to the database column of the table	Y	Y	Y	Column	2021-03-15 09:16:03.754	0	N	0	\N	\N	Y	Y
0	18	\N	0	10	2	Name	2020-01-24 13:56:57.745245	0			Y	N	N	Name	2020-01-24 13:56:57.745245	0	N	0	\N	\N	Y	S
0	54	a516b096-3298-4b7a-8718-4d67540f6fd1	0	10	6	Name	2020-01-11 14:51:33.421229	0	Alphanumeric identifier of the entity	\N	Y	N	Y	Name	2020-01-11 14:51:33.421229	0	N	0	\N	\N	Y	S
0	735	\N	0	11	56	CreatedBy	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	CreatedBy	2021-05-08 20:09:31.796	0	N	0	\N	\N	Y	N
0	736	\N	0	14	56	DefaultValue	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	N	DefaultValue	2021-05-08 20:09:41.24	0	N	0	\N	\N	Y	N
0	737	\N	0	10	56	Description	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	N	Description	2021-05-08 20:09:47.904	0	N	0	\N	\N	Y	N
0	20	\N	0	10	2	ClientId	2020-01-24 13:57:59.543117	0			Y	N	N	ClientId	2020-01-24 13:57:59.543117	0	N	0	\N	\N	Y	N
0	119	e24e056c-b6dc-4907-ade9-835080e528f5	0	19	6	CreatedBy	2020-01-11 14:51:52.618827	0	User who created this records	\N	Y	N	Y	Created By	2020-01-11 14:51:52.618827	0	N	0	\N	\N	Y	N
0	120	ec43aba8-ad5c-47fc-9ed8-a506310272a4	0	16	6	Updated	2020-01-11 14:51:52.632666	0	Date this record was updated	\N	Y	N	Y	Updated	2020-01-11 14:51:52.632666	0	N	0	\N	\N	Y	N
0	121	252360c9-57ae-4a00-8686-14a4b9d2fc54	0	19	6	UpdatedBy	2020-01-11 14:51:52.636688	0	User who updated this records	\N	Y	N	Y	Updated By	2020-01-11 14:51:52.636688	0	N	0	\N	\N	Y	N
0	61	6835ec07-25cc-405e-a1a0-703440584f7f	0	10	7	Help	2019-12-28 17:28:02.901006	0	Comment or Hint	The Help field contains a hint, comment or help about the use of this item.	Y	N	N	Comment/Help	2019-12-28 17:28:02.901006	0	N	0	\N	\N	Y	N
0	206	573ccdd8-0ff4-45a8-98f4-96c024dbf4ea	0	20	6	IsOrderByValue	2020-01-11 14:52:03.443386	100	Order list using the value column instead of the name column	\N	Y	N	N	Order By Value	2020-01-11 14:52:03.443386	100	N	0	\N	\N	Y	N
0	58	43f1b8e7-56f2-48ad-ae41-c13c2a9c1950	0	13	7	AD_Window_ID	2019-12-28 17:53:10.913217	0	Data entry or display window	\N	Y	Y	Y	Window	2019-12-28 17:53:10.913217	0	N	0	\N	\N	Y	N
0	60	7d8be06a-a943-4a93-a3e0-d113a42df3b3	0	10	7	Description	2019-12-28 17:53:10.922154	0	Optional short description of the record	\N	Y	N	N	Description	2019-12-28 17:53:10.922154	0	N	0	\N	\N	Y	N
0	92	37ceca04-7711-4e47-9224-ff825367e117	0	19	7	AD_Client_ID	2019-12-28 17:53:10.967264	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2019-12-28 17:53:10.967264	0	N	0	\N	\N	Y	N
0	93	5ab43067-3ffb-46f0-b38f-93781edb5f98	0	19	7	AD_Org_ID	2019-12-28 17:53:10.96989	0	Organizational entity within client	\N	Y	N	Y	Organization	2019-12-28 17:53:10.96989	0	N	0	\N	\N	Y	N
0	122	2ef554de-06e8-4a16-b9bf-797beaf905db	0	20	7	IsActive	2019-12-28 17:53:11.020896	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-28 17:53:11.020896	0	N	0	\N	\N	Y	N
0	123	aa6c8cb2-070e-475b-aa9c-57651a0b9c54	0	16	7	Created	2019-12-28 17:53:11.025764	0	Date this record was created	\N	Y	N	Y	Created	2019-12-28 17:53:11.025764	0	N	0	\N	\N	Y	N
0	124	25dc5501-5b49-489d-a92b-10a51ff29ecb	0	19	7	CreatedBy	2019-12-28 17:53:11.030154	0	User who created this records	\N	Y	N	Y	Created By	2019-12-28 17:53:11.030154	0	N	0	\N	\N	Y	N
0	125	578702d3-92b7-4bdf-948a-6b5c04b4005d	0	16	7	Updated	2019-12-28 17:53:11.03404	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-28 17:53:11.03404	0	N	0	\N	\N	Y	N
0	126	fc03f4ee-65c2-4431-9787-b9dec6283a68	0	19	7	UpdatedBy	2019-12-28 17:53:11.037521	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-28 17:53:11.037521	0	N	0	\N	\N	Y	N
0	65	54de41bd-457c-470a-a847-12b9089b88e0	0	10	8	Help	2019-12-28 17:26:50.44355	0	Comment or Hint	The Help field contains a hint, comment or help about the use of this item.	Y	N	N	Comment/Help	2019-12-28 17:26:50.44355	0	N	0	\N	\N	Y	N
0	62	e9ecc3a3-864d-4330-8f72-be349e739ac2	0	13	8	AD_Tab_ID	2019-12-28 17:53:10.926191	0	Tab within a Window	\N	Y	Y	Y	Tab	2019-12-28 17:53:10.926191	0	N	0	\N	\N	Y	N
0	64	a0641463-1417-4227-8f92-35ace3795727	0	10	8	Description	2019-12-28 17:53:10.934558	0	Optional short description of the record	\N	Y	N	N	Description	2019-12-28 17:53:10.934558	0	N	0	\N	\N	Y	N
0	66	1c800993-9d79-4d3f-86f4-63ccd43256c6	0	19	8	AD_Window_ID	2019-12-28 17:26:02.659628	0	Data entry or display window	The Window field identifies a unique Window in the system.	Y	N	Y	Window	2019-12-28 17:26:02.659628	0	N	0	\N	\N	Y	N
0	67	5d83b4fa-c440-4850-9857-7e95ba822949	0	11	8	SeqNo	2019-12-28 17:26:15.931578	0	Method of ordering records; lowest number comes first	The Sequence indicates the order of records	Y	N	Y	Sequence	2019-12-28 17:26:15.931578	0	N	0	\N	\N	Y	N
0	94	94d536cf-f8a7-41eb-9ddc-5864713d63eb	0	19	8	AD_Client_ID	2019-12-28 17:53:10.97244	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2019-12-28 17:53:10.97244	0	N	0	\N	\N	Y	N
0	95	781a028b-1238-4431-98b8-a46e8b50a5bc	0	19	8	AD_Org_ID	2019-12-28 17:53:10.975076	0	Organizational entity within client	\N	Y	N	Y	Organization	2019-12-28 17:53:10.975076	0	N	0	\N	\N	Y	N
0	127	7eb5c08b-fa2a-4703-89e5-e0db008a7902	0	20	8	IsActive	2019-12-28 17:53:11.040558	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-28 17:53:11.040558	0	N	0	\N	\N	Y	N
0	128	d38c6700-2f5f-4f00-8aab-aee672d7c76f	0	16	8	Created	2019-12-28 17:53:11.043446	0	Date this record was created	\N	Y	N	Y	Created	2019-12-28 17:53:11.043446	0	N	0	\N	\N	Y	N
0	129	7cb2a1ad-60a0-4aff-bdd1-fe447f9a9d35	0	19	8	CreatedBy	2019-12-28 17:53:11.046362	0	User who created this records	\N	Y	N	Y	Created By	2019-12-28 17:53:11.046362	0	N	0	\N	\N	Y	N
0	130	ae081bc2-fe10-46cf-8b45-48dfa11e90e6	0	16	8	Updated	2019-12-28 17:53:11.052462	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-28 17:53:11.052462	0	N	0	\N	\N	Y	N
0	131	599d170b-3b91-47e7-b92a-3820b78c7c6d	0	19	8	UpdatedBy	2019-12-28 17:53:11.057194	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-28 17:53:11.057194	0	N	0	\N	\N	Y	N
0	160	92596ea4-0109-40af-8fc4-c48d4f7eb694	0	20	8	IsReadOnly	2019-12-28 17:28:52.270676	0	Field is read only	The Read Only indicates that this field may only be Read.  It may not be updated.	Y	N	Y	Read Only	2019-12-28 17:28:52.270676	0	N	0	\N	\N	Y	N
0	192	65e7c9b4-122a-4110-8c29-6ce1e41dfe63	0	11	8	TabLevel	2019-12-28 17:53:11.102457	0	Hierarchical Tab Level (0 = top)	\N	Y	N	Y	Tab Level	2019-12-28 17:53:11.102457	0	N	0	\N	\N	Y	N
0	200	c08dce85-1020-4462-b95c-4252fb74b488	0	20	8	IsInsertRecord	2019-12-28 17:53:11.114593	100	The user can insert a new Record	\N	Y	N	Y	Insert Record	2019-12-28 17:53:11.114593	100	N	0	\N	\N	Y	N
0	75	9e260f94-8e6d-47da-b2b9-e7ef2b244a26	0	11	9	SeqNo	2019-12-30 21:23:38.335926	0	Method of ordering records; lowest number comes first	The Sequence indicates the order of records	Y	N	N	Sequence	2019-12-30 21:23:38.335926	0	N	0	\N	\N	Y	N
0	70	7b40870f-ee2a-4f52-8ae6-39b84aca41a4	0	10	9	Description	2019-12-28 17:53:10.940941	0	Optional short description of the record	\N	Y	N	N	Description	2019-12-28 17:53:10.940941	0	N	0	\N	\N	Y	N
0	533	\N	0	17	4	AccessLevel	2020-04-19 16:33:30.588985	0	Access Level required	Indicates the access level required for this record or process.	Y	N	N	AccessLevel	2020-04-19 16:33:30.588985	0	N	0	\N	45	Y	N
0	208	bccf3f87-42e6-43e6-bea6-0c890c112655	0	18	8	Parent_Column_ID	2019-12-28 17:53:11.133607	100	The link column on the parent tab.	\N	Y	N	N	Parent Column	2019-12-28 17:53:11.133607	0	N	0	\N	40	Y	N
0	739	\N	0	20	56	IsActive	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	IsActive	2021-05-08 20:10:07.858	0	N	0	\N	\N	Y	N
0	740	\N	0	20	56	IsMandatory	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	N	IsMandatory	2021-05-08 20:10:15.465	0	N	0	\N	\N	Y	N
0	555	\N	0	19	43	AD_Org_ID	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	Y	AD_Org_ID	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	553	\N	0	13	43	AD_User_App_ID	2020-06-15 17:49:48.35124	0	\N	\N	Y	Y	Y	AD_User_App_ID	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	551	\N	0	19	43	AD_User_ID	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	Y	AD_User_ID	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	68	d235e296-82eb-4a71-8998-e27e6d6c8f21	0	13	9	AD_Field_ID	2019-12-28 17:27:55.586874	0	Field on a database table	The Field identifies a field on a database table.	Y	Y	Y	Field	2021-03-15 09:23:01.736	0	N	0	\N	\N	Y	Y
0	69	57de2884-44c6-4ba0-bcdb-de631aa65a13	0	10	9	Label	2019-12-30 21:24:36.126123	0	Alphanumeric identifier of the entity	\N	Y	N	Y	Label	2021-03-15 09:26:13.868	0	N	0	\N	\N	Y	Y
0	59	0219ce5b-595b-4429-b62d-972d5968ab03	0	10	7	Name	2019-12-28 17:53:10.91722	0	Alphanumeric identifier of the entity	\N	Y	N	Y	Name	2019-12-28 17:53:10.91722	0	N	0	\N	\N	Y	S
0	153	fb9fc3c3-0321-43bf-aecf-11feed5cf1a7	0	16	13	Created	2019-12-31 15:24:13.28336	0	Date this record was created	\N	Y	N	Y	Created	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	N
0	72	ff9c6dd3-9abe-459f-bc85-0c4060ef4ad6	0	19	9	AD_Tab_ID	2019-12-28 17:26:43.453289	0	Tab within a Window	The Tab indicates a tab that displays within a window.	Y	N	Y	Tab	2019-12-28 17:26:43.453289	0	N	0	\N	\N	Y	N
0	73	4c590b34-5c11-4245-9da6-897b6c9d0d0b	0	19	9	AD_Column_ID	2019-12-28 17:53:10.943601	0	Column in the table	\N	Y	N	Y	Column	2019-12-28 17:53:10.943601	0	N	0	\N	\N	Y	N
0	74	28fdbd92-b235-4836-b6b6-96b0be84fd98	0	20	9	IsDisplayed	2019-12-28 17:53:10.946518	0	Determines, if this field is displayed	\N	Y	N	Y	Displayed	2019-12-28 17:53:10.946518	0	N	0	\N	\N	Y	N
0	76	3f79009b-f6ad-4537-a05d-0fd6c8c2d12c	0	20	9	IsSameLine	2019-12-28 17:27:02.620832	0	Displayed on same line as previous field	The Same Line checkbox indicates that the field will display on the same line as the previous field.	Y	N	Y	Same Line	2019-12-28 17:27:02.620832	0	N	0	\N	\N	Y	N
0	96	34d85526-9d62-442e-aa57-3f81e4ef819e	0	19	9	AD_Client_ID	2019-12-28 17:53:10.977506	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2019-12-28 17:53:10.977506	0	N	0	\N	\N	Y	N
0	97	cd3f025e-947a-4990-a05c-10de0d2f1036	0	19	9	AD_Org_ID	2019-12-28 17:53:10.980167	0	Organizational entity within client	\N	Y	N	Y	Organization	2019-12-28 17:53:10.980167	0	N	0	\N	\N	Y	N
0	132	c116f9f2-dd57-4a2f-9eb9-21946eb20d1e	0	20	9	IsActive	2019-12-28 17:53:11.061101	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-28 17:53:11.061101	0	N	0	\N	\N	Y	N
0	133	fff5f436-e5c0-456e-980d-dfd874dac6fe	0	16	9	Created	2019-12-28 17:53:11.064749	0	Date this record was created	\N	Y	N	Y	Created	2019-12-28 17:53:11.064749	0	N	0	\N	\N	Y	N
0	134	b193bcc3-aee6-40b6-bb3f-906ff75245be	0	19	9	CreatedBy	2019-12-28 17:53:11.068153	0	User who created this records	\N	Y	N	Y	Created By	2019-12-28 17:53:11.068153	0	N	0	\N	\N	Y	N
0	135	69abcf14-546e-46d2-be22-6d5a8584a34e	0	16	9	Updated	2019-12-28 17:53:11.071135	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-28 17:53:11.071135	0	N	0	\N	\N	Y	N
0	136	087bcec5-c830-4b58-ab06-b0ce82c7b39c	0	19	9	UpdatedBy	2019-12-28 17:53:11.077537	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-28 17:53:11.077537	0	N	0	\N	\N	Y	N
0	159	50ff9990-1d6b-4de5-a714-3fca8a7f0bbb	0	20	9	IsReadOnly	2019-12-28 17:28:31.005753	0	Field is read only	The Read Only indicates that this field may only be Read.  It may not be updated.	Y	N	Y	Read Only	2019-12-28 17:28:31.005753	0	N	0	\N	\N	Y	N
0	71	34233db0-d7f2-4241-a6dd-76dffd21d182	0	10	9	Help	2019-12-28 17:28:42.3848	0	Comment or Hint	The Help field contains a hint, comment or help about the use of this item.	Y	N	N	Comment/Help	2019-12-28 17:28:42.3848	0	N	0	\N	\N	Y	N
0	203	a167ccdb-5f85-46b4-b308-8e04433598d3	0	20	9	IsMandatory	2019-12-28 17:53:11.119286	100	Data entry is required in this column	\N	Y	N	N	Mandatory	2019-12-28 17:53:11.119286	100	N	0	\N	\N	Y	N
0	207	d6ed2b7a-95df-4e2e-b09c-7fe2c9b75655	0	10	9	Placeholder	2020-01-09 01:27:01.941922	100	Specifies a short hint that describes the expected value of an input field	\N	Y	N	N	Placeholder	2020-01-09 01:27:01.941922	100	N	0	\N	\N	Y	N
0	212	0eacadc5-a92e-4ca2-a1f7-ca9e463bc34f	0	20	9	IsDisplayedGrid	2020-01-09 01:33:16.125035	100		\N	Y	N	N	Show in Grid	2020-01-09 01:33:16.125035	100	N	0	\N	\N	Y	N
0	217	19bff665-ff23-4833-a825-073718edd435	0	10	9	BootstrapClass	2020-01-09 01:24:32.432698	100	Field CSS Style 	\N	Y	N	N	Bootstrap Class	2020-01-09 01:24:32.432698	100	N	0	\N	\N	Y	N
0	205	13455759-8395-4b02-a15a-c7035ae4ab2b	0	10	9	DefaultValue	2019-12-28 17:53:11.121793	0	Default value hierarchy, separated by ;	\N	Y	N	N	Default Value	2019-12-28 17:53:11.121793	0	N	0	\N	\N	Y	N
0	98	62bc5817-3f4a-43b4-b361-5f2f3b6206c2	0	19	10	AD_Client_ID	2019-12-31 15:16:08.382783	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2019-12-31 15:16:08.382783	0	N	0	\N	\N	Y	N
0	139	ea77d012-20c8-4373-9902-212038c133cc	0	19	10	CreatedBy	2019-12-31 15:16:08.382783	0	User who created this records	\N	Y	N	Y	Created By	2019-12-31 15:16:08.382783	0	N	0	\N	\N	Y	N
0	140	0fa2c9bd-9e16-4716-ae04-c35d35bfde0c	0	16	10	Updated	2019-12-31 15:16:08.382783	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-31 15:16:08.382783	0	N	0	\N	\N	Y	N
0	141	352f5742-acee-4560-8919-2f76557d2354	0	19	10	UpdatedBy	2019-12-31 15:16:08.382783	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-31 15:16:08.382783	0	N	0	\N	\N	Y	N
0	11	\N	0	19	2	AD_Client_ID	2020-01-24 13:54:30.96122	0			Y	N	N	AD_Client_ID	2020-01-24 13:54:30.96122	0	N	0	\N	\N	Y	N
0	19	\N	0	10	2	Description	2020-01-24 13:57:23.592047	0			Y	N	N	Description	2020-01-24 13:57:23.592047	0	N	0	\N	\N	Y	N
0	26	\N	0	19	3	AD_Org_ID	2020-01-27 09:20:18.400202	0			Y	N	N	Organization	2020-01-27 09:20:18.400202	0	N	0	\N	\N	Y	N
0	37	\N	0	10	3	RefreshToken	2020-01-27 09:25:06.766866	0			Y	N	N	Refresh Token	2020-01-27 09:25:06.766866	0	N	0	\N	\N	Y	N
0	38	\N	0	16	3	RefreshTokenExpiration	2020-01-27 09:25:40.061784	0			Y	N	N	Refresh Token Expiration	2020-01-27 09:25:40.061784	0	N	0	\N	\N	Y	N
0	40	\N	0	19	3	AD_User_ID	2020-01-27 10:06:10.969079	0			Y	N	N	User	2020-01-27 10:06:10.969079	0	N	0	\N	\N	Y	N
0	53	56ce7379-11d7-4612-8abb-e782cfffb193	0	13	6	AD_Reference_ID	2020-01-11 14:51:33.406613	0	System Reference and Validation	\N	Y	Y	Y	Reference	2020-01-11 14:51:33.406613	0	N	0	\N	\N	Y	N
0	99	f06fbc72-1e82-4420-99c2-a28554b9e6e1	0	19	10	AD_Org_ID	2019-12-31 15:16:08.382783	0	Organizational entity within client	\N	Y	N	Y	Organization	2019-12-31 15:16:08.382783	0	N	0	\N	\N	Y	N
0	137	df8dd7c4-5e30-43cf-882e-2ef7c79620d8	0	20	10	IsActive	2019-12-31 15:16:08.382783	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-31 15:16:08.382783	0	N	0	\N	\N	Y	N
0	138	ca0b38ed-7cda-4617-9a5a-75c4ec8305a6	0	16	10	Created	2019-12-31 15:16:08.382783	0	Date this record was created	\N	Y	N	Y	Created	2019-12-31 15:16:08.382783	0	N	0	\N	\N	Y	N
0	193	3c9104c8-30af-4738-9956-262fde1079aa	0	13	10	AD_Message_ID	2019-12-31 15:16:08.382783	0	System Message	\N	Y	Y	Y	Message	2019-12-31 15:16:08.382783	0	N	0	\N	\N	Y	N
0	79	ded92679-58aa-43e6-9bf2-fef7e7cf7925	0	10	10	MsgTip	2019-12-31 15:16:08.382783	0	Additional tip or help for this message	\N	Y	N	N	Message Tip	2019-12-31 15:16:08.382783	0	Y	0	\N	\N	Y	N
0	534	\N	0	17	12	UserLevel	2020-04-19 16:43:00.263394	0	\N	\N	Y	N	N	UserLevel	2020-04-19 16:43:00.263394	0	N	0	\N	46	Y	N
0	741	\N	0	20	56	IsSameLine	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	IsSameLine	2021-05-08 20:10:24.728	0	N	0	\N	\N	Y	N
0	398	\N	0	20	33	IsActive	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	IsActive	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	400	\N	0	18	33	CreatedBy	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	CreatedBy	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	742	\N	0	10	56	Label	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	Label	2021-05-08 20:10:30.697	0	N	0	\N	\N	Y	N
0	558	\N	0	18	43	CreatedBy	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	Y	CreatedBy	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	556	\N	0	20	43	IsActive	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	Y	IsActive	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	559	\N	0	16	43	Updated	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	Y	Updated	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	560	\N	0	18	43	UpdatedBy	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	Y	UpdatedBy	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	81	4ed5a1f6-0c27-4557-9b0c-4fb2756ba8cf	0	10	11	Name	2019-12-31 15:08:32.36048	0	Alphanumeric identifier of the entity	\N	Y	N	Y	Name	2021-03-12 23:30:20.985	0	N	0	\N	\N	Y	S
0	80	53c893bb-a914-45ef-91cb-d200671e9c6c	0	19	11	AD_Client_ID	2019-12-31 15:08:32.36048	0	Client/Tenant for this installation.	\N	Y	Y	Y	Client	2021-03-15 09:15:04.971	0	N	0	\N	\N	Y	N
0	241	\N	0	10	21	AD_Object_Access_UU	2020-01-30 07:57:56.157636	0	\N	\N	Y	N	N	AD_Object_Access_UU	2020-01-30 07:57:56.157636	0	N	0	\N	\N	Y	N
0	82	6693fe56-02f3-4688-8d16-45d51c5131b0	0	10	11	Description	2019-12-31 15:08:32.36048	0	Optional short description of the record	\N	Y	N	N	Description	2019-12-31 15:08:32.36048	0	N	0	\N	\N	Y	N
0	100	a63c8d41-f568-4364-bf59-08deac044721	0	19	11	AD_Org_ID	2019-12-31 15:08:32.36048	0	Organizational entity within client	\N	Y	N	Y	Organization	2019-12-31 15:08:32.36048	0	N	0	\N	\N	Y	N
0	142	16c7565f-712a-4d16-9632-fb1ed17ff9e3	0	20	11	IsActive	2019-12-31 15:08:32.36048	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-31 15:08:32.36048	0	N	0	\N	\N	Y	N
0	143	d0f2b92f-46a2-4c9a-901f-54e0d4f98257	0	16	11	Created	2019-12-31 15:08:32.36048	0	Date this record was created	\N	Y	N	Y	Created	2019-12-31 15:08:32.36048	0	N	0	\N	\N	Y	N
0	144	608975ff-d6ea-412b-af08-bb1b5b0bb9af	0	19	11	CreatedBy	2019-12-31 15:08:32.36048	0	User who created this records	\N	Y	N	Y	Created By	2019-12-31 15:08:32.36048	0	N	0	\N	\N	Y	N
0	145	fbb7d861-f2e1-40cd-82d1-d1b5f356f2d2	0	16	11	Updated	2019-12-31 15:08:32.36048	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-31 15:08:32.36048	0	N	0	\N	\N	Y	N
0	146	18bb150d-6e7b-4aa7-8437-d22fc6b65fd9	0	19	11	UpdatedBy	2019-12-31 15:08:32.36048	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-31 15:08:32.36048	0	N	0	\N	\N	Y	N
0	191	c21425fe-b8bc-4243-b8cd-a7f6b4f382f0	0	10	11	AD_Language	2019-12-31 15:10:16.772872	0	Language for this entity	\N	Y	N	N	Language	2019-12-31 15:10:16.772872	100	N	0	\N	\N	Y	N
0	204	9f721e7a-e1c2-4fad-8e86-c39682845e5f	0	42	12	UserPIN	2019-12-31 16:08:40.754147	0		\N	Y	N	N	User PIN	2019-12-31 16:08:40.754147	0	N	0	\N	\N	Y	N
0	101	e8d1a469-df85-4a64-85bb-f3927a83bebd	0	19	12	AD_Client_ID	2019-12-31 16:08:40.754147	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2019-12-31 16:08:40.754147	100	N	0	\N	\N	Y	N
0	147	c66316e8-417d-4584-88e1-714f76ab3574	0	20	12	IsActive	2019-12-31 16:08:40.754147	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-31 16:08:40.754147	100	N	0	\N	\N	Y	N
0	148	997d28ee-8032-47a6-873b-afbfa7fb8bf3	0	16	12	Created	2019-12-31 16:08:40.754147	0	Date this record was created	\N	Y	N	Y	Created	2019-12-31 16:08:40.754147	100	N	0	\N	\N	Y	N
0	150	43098019-b95c-4dde-bb71-c2afa39ce2a8	0	16	12	Updated	2019-12-31 16:08:40.754147	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-31 16:08:40.754147	100	N	0	\N	\N	Y	N
0	198	464c6689-b429-4209-9efc-365220635f89	0	10	12	EMailUser	2019-12-31 16:08:40.754147	0	User Name (ID) in the Mail System	\N	Y	N	N	EMail User ID	2019-12-31 16:08:40.754147	100	N	0	\N	\N	Y	N
0	211	b956aea4-4028-42f0-a979-5f48d39ae8b1	0	10	12	AD_User_UU	2019-12-31 16:08:40.754147	100		\N	Y	N	N	AD_User_UU	2019-12-31 16:08:40.754147	100	N	0	\N	\N	Y	N
0	213	fb4326ef-4bb7-405e-8e76-5d0a178c6f70	0	20	12	IsLocked	2019-12-31 16:08:40.754147	100		\N	Y	N	Y	Locked	2019-12-31 16:08:40.754147	100	N	0	\N	\N	Y	N
0	215	6885fa87-4608-418c-a5d7-d94875c7ac91	0	16	12	DateLastLogin	2019-12-31 16:08:40.754147	100		\N	Y	N	N	Date Last Login	2019-12-31 16:08:40.754147	100	N	0	\N	\N	Y	N
0	104	95d94567-f8fd-4b05-9667-6717e96dc226	0	10	13	Description	2019-12-31 15:24:13.28336	0	Optional short description of the record	\N	Y	N	N	Description	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	N
0	152	b43d4ca7-e026-40a6-808b-9d9e9770c0f2	0	20	13	IsActive	2019-12-31 15:24:13.28336	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	N
0	154	d6d6e425-9152-4e14-ad33-7f0e60f8fcce	0	19	13	CreatedBy	2019-12-31 15:24:13.28336	0	User who created this records	\N	Y	N	Y	Created By	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	N
0	155	6a422bed-38e4-4d53-99d8-e7271de73d4d	0	16	13	Updated	2019-12-31 15:24:13.28336	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	N
0	105	d78cf2ee-109d-4e81-8f49-ff3c632a5ebd	0	19	13	AD_Client_ID	2019-12-31 15:24:13.28336	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	N
0	106	cd831130-f810-4e8c-8401-16231aa7d3c4	0	19	13	AD_Org_ID	2019-12-31 15:24:13.28336	0	Organizational entity within client	\N	Y	Y	Y	Organization	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	N
0	156	1e196cbc-362d-4803-9d4b-835fdbee2927	0	19	13	UpdatedBy	2019-12-31 15:24:13.28336	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	N
0	162	4feb51f6-9459-40c1-8e62-b4b235c20a33	0	13	14	AD_Tree_ID	2019-12-31 15:30:55.741282	0	Identifies a Tree	\N	Y	Y	Y	Tree	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	N
0	165	cb5453a9-b5e0-4f4c-a80f-7d7aa2e6783a	0	16	14	Created	2019-12-31 15:30:55.741282	0	Date this record was created	\N	Y	N	Y	Created	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	N
0	167	1ec50731-f642-4e48-b944-ab8f40573eac	0	16	14	Updated	2019-12-31 15:30:55.741282	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	N
0	163	f64e6e49-ca10-4e8b-b352-d44ef7be8fef	0	19	14	AD_Client_ID	2019-12-31 15:30:55.741282	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	N
0	164	48ee967f-6f4f-4073-92f0-4d655e10b7f5	0	19	14	AD_Org_ID	2019-12-31 15:30:55.741282	0	Organizational entity within client	\N	Y	N	Y	Organization	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	N
0	166	15518e43-3356-4471-be44-3f931775b049	0	19	14	CreatedBy	2019-12-31 15:30:55.741282	0	User who created this records	\N	Y	N	Y	Created By	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	N
0	168	6f003536-4b90-4940-b44c-af52e4e2b246	0	19	14	UpdatedBy	2019-12-31 15:30:55.741282	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	N
0	535	\N	0	19	2	AD_User_ID	2020-04-19 20:24:32.110773	0	\N	\N	Y	N	N	AD_User_ID	2020-04-19 20:24:32.110773	0	N	0	\N	\N	Y	N
0	402	\N	0	18	33	UpdatedBy	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	UpdatedBy	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	102	f741f322-8e2c-4ebe-9ed3-26b4993c6694	0	19	12	AD_Org_ID	2019-12-31 16:08:40.754147	0	Organizational entity within client	\N	Y	N	Y	AD_Org_ID	2019-12-31 16:08:40.754147	0	N	0	\N	\N	Y	N
0	149	b4156b00-a9ed-41e2-9eb8-6500e3fcb7c8	0	18	12	CreatedBy	2019-12-31 16:08:40.754147	0	User who created this records	\N	Y	N	Y	Created By	2019-12-31 16:08:40.754147	0	N	0	\N	\N	Y	N
0	151	3f07192a-a7f5-4596-9ab2-31ba3bf9d814	0	18	12	UpdatedBy	2019-12-31 16:08:40.754147	0	User who updated this records	\N	Y	N	Y	Updated By	2019-12-31 16:08:40.754147	0	N	0	\N	\N	Y	N
0	214	c7f6d455-4956-4a11-a528-ee007dd3c49d	0	15	12	DateAccountLocked	2019-12-31 16:08:40.754147	100		\N	Y	N	N	Date Account Locked	2019-12-31 16:08:40.754147	0	N	0	\N	\N	Y	N
0	561	\N	0	10	43	AD_User_App_UU	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	N	AD_User_App_UU	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	744	\N	0	11	56	SeqNo	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	N	SeqNo	2021-05-08 20:10:43.732	0	N	0	\N	\N	Y	N
0	745	\N	0	16	56	Updated	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	Updated	2021-05-08 20:10:49.86	0	N	0	\N	\N	Y	N
0	103	adfed9b7-7579-4577-92bf-9fe83ac6708c	0	10	13	Name	2019-12-31 15:24:13.28336	0	Alphanumeric identifier of the entity	\N	Y	N	Y	Name	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	S
0	185	f6989624-6843-45bd-8650-704eb7ea3599	0	10	11	Value	2019-12-31 15:08:32.36048	0	Search key for the record in the format required - must be unique	\N	Y	N	Y	Search Key	2019-12-31 15:08:32.36048	0	N	0	\N	\N	Y	S
0	44	bcc2761d-1ea0-4187-b3f4-0e137fee0bae	0	10	4	Help	2019-12-28 17:26:30.874044	0	Comment or Hint	The Help field contains a hint, comment or help about the use of this item.	Y	N	N	Comment/Help	2019-12-28 17:26:30.874044	0	N	0	\N	\N	Y	N
0	312	\N	0	13	28	AD_Media_ID	2020-03-07 19:50:02.359401	0	\N	\N	Y	Y	Y	AD_Media_ID	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	305	\N	0	18	27	CreatedBy	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	Y	CreatedBy	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	3	\N	0	19	1	AD_Org_ID	2020-01-22 20:39:32.14252	0			Y	N	N	Organization	2020-01-22 20:39:32.14252	0	N	0	\N	\N	Y	N
0	5	\N	0	16	1	Created	2020-01-22 20:40:14.490399	0			Y	N	N	Created	2020-01-22 20:40:14.490399	0	N	0	\N	\N	Y	N
0	6	\N	0	18	1	CreatedBy	2020-01-22 20:40:45.735639	0			Y	N	N	CreatedBy	2020-01-22 20:40:45.735639	0	N	0	\N	\N	Y	N
0	7	\N	0	16	1	Updated	2020-01-22 20:41:02.560081	0			Y	N	N	Updated	2020-01-22 20:41:02.560081	0	N	0	\N	\N	Y	N
0	9	\N	0	10	1	AD_Resource_Type_UU	2020-01-22 20:42:34.993828	0			Y	N	N	AD_Resource_Type_UU	2020-01-22 20:42:34.993828	0	N	0	\N	\N	Y	N
0	12	\N	0	19	2	AD_Org_ID	2020-01-24 13:55:16.813212	0			Y	N	N	AD_Org_ID	2020-01-24 13:55:16.813212	0	N	0	\N	\N	Y	N
0	13	\N	0	20	2	IsActive	2020-01-24 13:55:38.865532	0			Y	N	N	IsActive	2020-01-24 13:55:38.865532	0	N	0	\N	\N	Y	N
0	14	\N	0	16	2	Created	2020-01-24 13:55:57.817452	0			Y	N	N	Created	2020-01-24 13:55:57.817452	0	N	0	\N	\N	Y	N
0	15	\N	0	18	2	CreatedBy	2020-01-24 13:56:11.968867	0			Y	N	N	CreatedBy	2020-01-24 13:56:11.968867	0	N	0	\N	\N	Y	N
0	16	\N	0	16	2	Updated	2020-01-24 13:56:26.761417	0			Y	N	N	Updated	2020-01-24 13:56:26.761417	0	N	0	\N	\N	Y	N
0	17	\N	0	18	2	UpdatedBy	2020-01-24 13:56:42.64395	0			Y	N	N	UpdatedBy	2020-01-24 13:56:42.64395	0	N	0	\N	\N	Y	N
0	35	\N	0	16	3	AuthorizationCodeExpiration	2020-01-27 09:23:35.756434	0			Y	N	N	Authorization Code Expiration	2020-01-27 09:23:35.756434	0	N	0	\N	\N	Y	N
0	36	\N	0	20	3	IsActiveAccessToken	2020-01-27 09:24:04.788596	0			Y	N	N	Active Access Token	2020-01-27 09:24:04.788596	0	N	0	\N	\N	Y	N
0	39	\N	0	19	3	AD_OAuth2_Client_ID	2020-01-27 09:26:09.694525	0			Y	N	N	OAuth2 Client	2020-01-27 09:26:09.694525	0	N	0	\N	\N	Y	N
0	41	6f2dd680-003d-4112-a379-1fe8721f2758	0	13	4	AD_Table_ID	2019-12-28 17:24:10.670168	0	Database Table information	The Database Table provides the information of the table definition	Y	Y	Y	Table	2019-12-28 17:24:10.670168	0	N	0	\N	\N	Y	N
0	43	5a96bea4-5cc1-4e00-90a0-5e45188fb0a2	0	10	4	Description	2019-12-28 17:53:10.882208	0	Optional short description of the record	\N	Y	N	N	Description	2019-12-28 17:53:10.882208	0	N	0	\N	\N	Y	N
0	45	59b883da-0d85-449d-8378-a5e0325b9e43	0	10	4	TableName	2019-12-28 17:53:10.88798	0	Name of the table in the database	\N	Y	N	Y	DB Table Name	2019-12-28 17:53:10.88798	100	N	0	\N	\N	Y	N
0	86	18c81988-4dd7-4370-b642-4f524a95b0f7	0	19	4	AD_Client_ID	2019-12-28 17:53:10.955963	0	Client/Tenant for this installation.	\N	Y	N	Y	Client	2019-12-28 17:53:10.955963	0	N	0	\N	\N	Y	N
0	87	c66527e4-5475-43cc-9fcc-526d3002e5ae	0	19	4	AD_Org_ID	2019-12-28 17:53:10.958672	0	Organizational entity within client	\N	Y	N	Y	Organization	2019-12-28 17:53:10.958672	0	N	0	\N	\N	Y	N
0	107	7eebab87-a088-4d44-ade3-c5faee095d18	0	20	4	IsActive	2019-12-28 17:53:10.982786	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-28 17:53:10.982786	0	N	0	\N	\N	Y	N
0	313	\N	0	19	28	AD_Client_ID	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	AD_Client_ID	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	169	c36a097c-db4a-4123-b74f-deb30d196aaf	0	20	14	IsActive	2019-12-31 15:30:55.741282	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	N
0	746	\N	0	11	56	UpdatedBy	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	UpdatedBy	2021-05-08 20:10:56.115	0	N	0	\N	\N	Y	N
0	666	\N	0	10	51	Header	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	Header	2021-01-01 14:03:11.016	0	N	0	\N	\N	Y	N
0	536	\N	0	11	2	RefreshTokenValidity	2020-04-23 13:51:32.709696	0	\N	\N	Y	N	N	RefreshTokenValidity	2020-04-23 13:51:32.709696	0	N	0	\N	\N	Y	N
0	537	\N	0	20	2	IsRefreshTokenExpires	2020-04-23 13:51:57.018894	0	\N	\N	Y	N	N	IsRefreshTokenExpires	2020-04-23 13:51:57.018894	0	N	0	\N	\N	Y	N
0	407	\N	0	19	33	AD_Extension_ID	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	AD_Extension_ID	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	406	\N	0	19	33	AD_Reference_ID	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	AD_Reference_ID	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	408	\N	0	10	33	AD_Ref_List_UU	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	N	AD_Ref_List_UU	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	405	\N	0	10	33	Description	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	N	Description	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	83	92311b98-72e2-4fc3-a967-d9e8c39c3d4b	0	13	12	AD_User_ID	2019-12-31 16:08:40.754147	0	User within the system - Internal or Business Partner Contact	\N	Y	Y	Y	AD_User_ID	2019-12-31 16:08:40.754147	0	N	0	\N	\N	Y	N
0	10	\N	0	13	2	AD_OAuth2_Client_ID	2020-01-24 13:53:52.947792	0			Y	Y	N	AD_OAuth2_Client_ID	2020-01-24 13:53:52.947792	0	N	0	\N	\N	Y	N
0	8	\N	0	18	1	UpdatedBy	2020-01-22 20:41:29.510047	0			Y	N	N	UpdatedBy	2020-01-22 20:41:29.510047	0	N	0	\N	\N	Y	N
0	449	\N	0	10	30	Method	2020-03-27 20:15:41.920473	0			Y	N	N	Method	2020-03-27 20:15:41.920473	0	N	0	\N	\N	Y	N
0	554	\N	0	19	43	AD_Client_ID	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	Y	AD_Client_ID	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	513	\N	0	10	40	AD_NotificationTemplate_UU	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	N	AD_NotificationTemplate_UU	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	510	\N	0	20	40	IsParseTemplate	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	IsParseTemplate	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	506	\N	0	16	40	Updated	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	Updated	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	507	\N	0	11	40	UpdatedBy	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	UpdatedBy	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	564	\N	0	19	44	AD_Org_ID	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	AD_Org_ID	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	570	\N	0	19	44	AD_Role_ID	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	AD_Role_ID	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	562	\N	0	13	44	AD_Window_Access_ID	2020-06-16 19:54:18.28775	0	\N	\N	Y	Y	Y	AD_Window_Access_ID	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	573	\N	0	10	44	AD_Window_Access_UU	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	N	AD_Window_Access_UU	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	571	\N	0	19	44	AD_Window_ID	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	AD_Window_ID	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	566	\N	0	16	44	Created	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	Created	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	567	\N	0	18	44	CreatedBy	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	CreatedBy	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	565	\N	0	20	44	IsActive	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	IsActive	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	572	\N	0	20	44	ReadOnly	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	ReadOnly	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	568	\N	0	16	44	Updated	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	Updated	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	569	\N	0	18	44	UpdatedBy	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	UpdatedBy	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	511	\N	0	10	40	Header	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	Header	2021-01-01 14:01:42.292	0	Y	0	\N	\N	Y	N
0	512	\N	0	14	40	Template	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	Template	2021-01-01 14:01:52.226	0	Y	0	\N	\N	Y	N
0	404	\N	0	10	33	Name	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	Name	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	S
0	396	\N	0	19	33	AD_Client_ID	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	AD_Client_ID	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	399	\N	0	16	33	Created	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	Created	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	274	\N	0	13	26	AD_Extension_ID	2020-02-23 01:22:37.244846	0	\N	\N	Y	Y	Y	AD_Extension	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	538	\N	0	20	3	IsActiveRefreshToken	2020-04-23 14:08:26.354086	0	\N	\N	Y	N	N	IsActiveRefreshToken	2020-04-23 14:08:26.354086	0	N	0	\N	\N	Y	N
0	450	\N	0	20	4	IsPublic	2020-04-06 19:08:25.353322	0	\N	\N	Y	N	N	IsPublic	2020-04-06 19:08:25.353322	0	N	0	\N	\N	Y	N
0	453	\N	0	20	21	IsExactlyMatch	2020-04-10 11:12:12.797921	0	\N	\N	Y	N	N	IsExactlyMatch	2020-04-10 11:12:12.797921	0	N	0	\N	\N	Y	N
0	501	\N	0	19	40	AD_Client_ID	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	AD_Client_ID	2020-04-16 08:56:34.354575	0	N	0	\N	\N	Y	N
0	708	\N	0	10	54	Help	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	N	Help	2021-05-06 20:41:17.264	0	N	0	\N	\N	Y	N
0	563	\N	0	19	44	AD_Client_ID	2020-06-16 19:54:18.28775	0	\N	\N	Y	N	Y	AD_Client_ID	2020-06-16 19:54:18.28775	0	N	0	\N	\N	Y	N
0	574	\N	0	20	5	Updatable	2020-08-08 15:58:48.306331	0	The Updatable checkbox indicates if a field can be updated by the user.	\N	Y	N	N	Updatable	2020-08-08 15:58:48.306331	0	N	0	\N	\N	Y	N
0	701	\N	0	20	54	IsActive	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	Y	IsActive	2021-05-06 20:41:24.367	0	N	0	\N	\N	Y	N
0	709	\N	0	10	54	ProcedureName	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	N	ProcedureName	2021-05-06 20:41:33.313	0	N	0	\N	\N	Y	N
0	668	\N	0	10	51	AD_Language	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	AD_Language	2021-01-01 14:02:32.458	0	N	0	\N	\N	Y	N
0	667	\N	0	14	51	Template	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	Template	2021-01-01 14:03:29.052	0	N	0	\N	\N	Y	N
0	704	\N	0	16	54	Updated	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	Y	Updated	2021-05-06 20:41:40.198	0	N	0	\N	\N	Y	N
0	705	\N	0	11	54	UpdatedBy	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	Y	UpdatedBy	2021-05-06 20:41:47.499	0	N	0	\N	\N	Y	N
0	706	\N	0	10	54	Value	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	Y	Value	2021-05-06 20:41:56.261	0	N	0	\N	\N	Y	Y
0	707	\N	0	10	54	Description	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	N	Description	2021-05-06 20:42:06.609	0	N	0	\N	\N	Y	Y
0	714	\N	0	19	55	AD_Client_ID	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	AD_Client_ID	2021-05-06 20:42:45.557	0	N	0	\N	\N	Y	N
0	715	\N	0	19	55	AD_Org_ID	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	AD_Org_ID	2021-05-06 20:42:52.771	0	N	0	\N	\N	Y	N
0	713	\N	0	13	55	AD_ToolBarButton_ID	2021-05-06 20:39:33.474519	0	\N	\N	Y	Y	Y	AD_ToolBarButton_ID	2021-05-06 20:43:27.424	0	N	0	\N	\N	Y	N
0	717	\N	0	16	55	Created	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	Created	2021-05-06 20:43:48.728	0	N	0	\N	\N	Y	N
0	718	\N	0	11	55	CreatedBy	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	CreatedBy	2021-05-06 20:43:55.348	0	N	0	\N	\N	Y	N
0	722	\N	0	10	55	Icon	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	N	Icon	2021-05-06 20:44:15.329	0	N	0	\N	\N	Y	N
0	716	\N	0	20	55	IsActive	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	IsActive	2021-05-06 20:44:22.358	0	N	0	\N	\N	Y	N
0	671	\N	0	13	52	AD_App_ID	2021-02-06 15:23:45.315687	0	\N	\N	Y	Y	Y	AD_App_ID	2021-02-06 16:28:26.941	0	N	0	\N	\N	Y	N
0	681	\N	0	10	52	AD_App_UU	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	N	AD_App_UU	2021-02-06 16:28:34.264	0	N	0	\N	\N	Y	N
0	672	\N	0	19	52	AD_Client_ID	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	Y	AD_Client_ID	2021-02-06 16:28:41.825	0	N	0	\N	\N	Y	N
0	673	\N	0	19	52	AD_Org_ID	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	Y	AD_Org_ID	2021-02-06 16:28:49.681	0	N	0	\N	\N	Y	N
0	675	\N	0	16	52	Created	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	Y	Created	2021-02-06 16:28:56.475	0	N	0	\N	\N	Y	N
0	676	\N	0	11	52	CreatedBy	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	Y	CreatedBy	2021-02-06 16:29:03.57	0	N	0	\N	\N	Y	N
0	680	\N	0	10	52	Description	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	N	Description	2021-02-06 16:29:10.836	0	N	0	\N	\N	Y	N
0	674	\N	0	20	52	IsActive	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	Y	IsActive	2021-02-06 16:29:18.498	0	N	0	\N	\N	Y	N
0	677	\N	0	16	52	Updated	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	Y	Updated	2021-02-06 16:29:25.272	0	N	0	\N	\N	Y	N
0	678	\N	0	11	52	UpdatedBy	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	Y	UpdatedBy	2021-02-06 16:29:31.85	0	N	0	\N	\N	Y	N
0	552	\N	0	19	43	AD_App_ID	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	Y	AD_App_ID	2021-02-06 16:30:13.577	0	N	0	\N	\N	Y	N
0	699	\N	0	19	54	AD_Client_ID	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	Y	AD_Client_ID	2021-05-06 20:40:07.678	0	N	0	\N	\N	Y	N
0	679	\N	0	10	52	Value	2021-02-06 15:23:45.315687	0	\N	\N	Y	N	Y	Value	2021-03-15 09:10:28.312	0	N	0	\N	\N	Y	S
0	721	\N	0	10	55	Name	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	Name	2021-05-06 20:44:43.156	0	N	0	\N	\N	Y	Y
0	719	\N	0	16	55	Updated	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	Updated	2021-05-06 20:44:50.431	0	N	0	\N	\N	Y	N
0	720	\N	0	11	55	UpdatedBy	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	UpdatedBy	2021-05-06 20:44:56.71	0	N	0	\N	\N	Y	N
0	730	\N	0	19	56	AD_Client_ID	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	AD_Client_ID	2021-05-08 20:08:29.206	0	N	0	\N	\N	Y	N
0	749	\N	0	19	56	AD_Extension_ID	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	AD_Extension_ID	2021-05-08 20:08:40.585	0	N	0	\N	\N	Y	N
0	747	\N	0	19	56	AD_Process_ID	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	AD_Process_ID	2021-05-08 20:08:53.668	0	N	0	\N	\N	Y	N
0	710	\N	0	19	54	AD_Extension_ID	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	N	AD_Extension_ID	2021-05-06 20:40:16.146	0	N	0	\N	\N	Y	N
0	700	\N	0	19	54	AD_Org_ID	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	Y	AD_Org_ID	2021-05-06 20:40:23.578	0	N	0	\N	\N	Y	N
0	698	\N	0	13	54	AD_Process_ID	2021-05-06 20:39:22.376612	0	\N	\N	Y	Y	Y	AD_Process_ID	2021-05-06 20:40:31.682	0	N	0	\N	\N	Y	N
0	712	\N	0	10	54	AD_Process_UU	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	N	AD_Process_UU	2021-05-06 20:40:41.016	0	N	0	\N	\N	Y	N
0	711	\N	0	19	54	AD_Scripting_ID	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	N	AD_Scripting_ID	2021-05-06 20:40:50.968	0	N	0	\N	\N	Y	N
0	702	\N	0	16	54	Created	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	Y	Created	2021-05-06 20:40:56.11	0	N	0	\N	\N	Y	N
0	703	\N	0	11	54	CreatedBy	2021-05-06 20:39:22.376612	0	\N	\N	Y	N	Y	CreatedBy	2021-05-06 20:41:04.607	0	N	0	\N	\N	Y	N
0	748	\N	0	10	56	BootstrapClass	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	N	BootstrapClass	2021-05-08 20:09:18.653	0	N	0	\N	\N	Y	N
0	734	\N	0	16	56	Created	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	Y	Created	2021-05-08 20:09:25.31	0	N	0	\N	\N	Y	N
0	750	\N	0	10	56	DynamicValidation	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	N	DynamicValidation	2021-05-08 20:09:55.287	0	N	0	\N	\N	Y	N
0	738	\N	0	14	56	Help	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	N	Help	2021-05-08 20:10:01.128	0	N	0	\N	\N	Y	N
0	743	\N	0	10	56	Placeholder	2021-05-08 20:06:04.603468	0	\N	\N	Y	N	N	PlaceHolder	2021-05-08 22:04:50.884	0	N	0	\N	\N	Y	N
0	397	\N	0	19	33	AD_Org_ID	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	AD_Org_ID	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	525	\N	0	42	41	RequestUserPW	2020-04-16 09:17:56.690668	0	Password of the user name (ID) for mail processing	\N	Y	N	Y	RequestUserPW	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	539	\N	0	20	12	IsAdmin	2020-04-26 21:18:02.803058	0	\N	\N	Y	N	N	IsAdmin	2020-04-26 21:18:02.803058	0	N	0	\N	\N	Y	N
0	412	\N	0	13	21	AD_Object_Access_ID	2020-03-27 18:40:12.618246	0			Y	Y	N	AD_Object_Access_ID	2020-03-27 18:40:12.618246	0	N	0	\N	\N	Y	N
0	454	\N	0	10	27	Classname	2020-04-10 13:01:43.928015	0	\N	\N	Y	N	N	Classname	2020-04-10 13:01:43.928015	0	N	0	\N	\N	Y	N
0	557	\N	0	16	43	Created	2020-06-15 17:49:48.35124	0	\N	\N	Y	N	Y	Created	2020-06-15 17:49:48.35124	0	N	0	\N	\N	Y	N
0	723	\N	0	10	55	Description	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	N	Description	2021-05-06 20:44:02.422	0	N	0	\N	\N	Y	N
0	724	\N	0	10	55	Help	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	N	Help	2021-05-06 20:44:08.897	0	N	0	\N	\N	Y	N
0	576	\N	0	19	45	AD_Client_ID	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	Y	AD_Client_ID	2020-09-14 20:06:23.804	0	N	0	\N	\N	Y	N
0	577	\N	0	19	45	AD_Org_ID	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	Y	AD_Org_ID	2020-09-14 20:06:30.665	0	N	0	\N	\N	Y	N
0	515	\N	0	19	41	AD_Client_ID	2020-04-16 09:17:56.690668	0	\N	\N	Y	N	Y	AD_Client_ID	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	514	\N	0	13	41	AD_MailConfig_ID	2020-04-16 09:17:56.690668	0	\N	\N	Y	Y	Y	AD_MailConfig_ID	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	528	\N	0	10	41	AD_MailConfig_UU	2020-04-16 09:17:56.690668	0	\N	\N	Y	N	N	AD_MailConfig_UU	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	516	\N	0	19	41	AD_Org_ID	2020-04-16 09:17:56.690668	0	\N	\N	Y	N	Y	AD_Org_ID	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	518	\N	0	16	41	Created	2020-04-16 09:17:56.690668	0	\N	\N	Y	N	Y	Created	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	517	\N	0	20	41	IsActive	2020-04-16 09:17:56.690668	0	\N	\N	Y	N	Y	IsActive	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	520	\N	0	16	41	Updated	2020-04-16 09:17:56.690668	0	\N	\N	Y	N	Y	Updated	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	521	\N	0	18	41	UpdatedBy	2020-04-16 09:17:56.690668	0	\N	\N	Y	N	Y	UpdatedBy	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	575	\N	0	13	45	AD_SysConfig_ID	2020-09-14 20:05:49.333815	0	\N	\N	Y	Y	Y	AD_SysConfig_ID	2020-09-14 20:06:40.938	0	N	0	\N	\N	Y	N
0	586	\N	0	10	45	AD_SysConfig_UU	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	N	AD_SysConfig_UU	2020-09-14 20:06:47.198	0	N	0	\N	\N	Y	N
0	519	\N	0	18	41	CreatedBy	2020-04-16 09:17:56.690668	0	\N	\N	Y	N	Y	CreatedBy	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	522	\N	0	10	41	RequestEmail	2020-04-16 09:17:56.690668	0	EMail address to send automated mails from or receive mails for automated processing (fully qualified)	\N	Y	N	Y	RequestEmail	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	579	\N	0	16	45	Created	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	Y	Created	2020-09-14 20:06:53.614	0	N	0	\N	\N	Y	N
0	523	\N	0	10	41	RequestFolder	2020-04-16 09:17:56.690668	0	EMail folder to process incoming emails; if empty INBOX is used	\N	Y	N	Y	RequestFolder	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	524	\N	0	10	41	RequestUser	2020-04-16 09:17:56.690668	0	User Name (ID) of the email owner	\N	Y	N	Y	RequestUser	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	587	\N	0	13	46	AD_AppRule_ID	2020-10-29 09:48:28.577669	0	\N	\N	Y	Y	Y	AD_AppRule_ID	2020-10-29 09:51:14.178	0	N	0	\N	\N	Y	N
0	526	\N	0	10	41	SMTPHost	2020-04-16 09:17:56.690668	0	Send EMail from Server	\N	Y	N	Y	SMTPHost	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	527	\N	0	11	41	SMTPPort	2020-04-16 09:17:56.690668	0	SMTP Port Number	\N	Y	N	Y	SMTPPort	2020-04-16 09:17:56.690668	0	N	0	\N	\N	Y	N
0	580	\N	0	11	45	CreatedBy	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	Y	CreatedBy	2020-09-14 20:07:01.075	0	N	0	\N	\N	Y	N
0	585	\N	0	10	45	Description	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	N	Description	2020-09-14 20:07:07.417	0	N	0	\N	\N	Y	N
0	578	\N	0	20	45	IsActive	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	Y	IsActive	2020-09-14 20:07:13.795	0	N	0	\N	\N	Y	N
0	581	\N	0	16	45	Updated	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	Y	Updated	2020-09-14 20:07:24.387	0	N	0	\N	\N	Y	N
0	582	\N	0	11	45	UpdatedBy	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	Y	UpdatedBy	2020-09-14 20:07:32.434	0	N	0	\N	\N	Y	N
0	598	\N	0	10	46	AD_AppRule_UU	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	N	AD_AppRule_UU	2020-10-29 09:51:21.966	0	N	0	\N	\N	Y	N
0	588	\N	0	19	46	AD_Client_ID	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	Y	AD_Client_ID	2020-10-29 09:51:28.894	0	N	0	\N	\N	Y	N
0	589	\N	0	19	46	AD_Org_ID	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	Y	AD_Org_ID	2020-10-29 09:51:46.806	0	N	0	\N	\N	Y	N
0	596	\N	0	19	46	AD_Table_ID	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	Y	AD_Table_ID	2020-10-29 09:51:53.273	0	N	0	\N	\N	Y	N
0	591	\N	0	16	46	Created	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	Y	Created	2020-10-29 09:51:59.42	0	N	0	\N	\N	Y	N
0	592	\N	0	11	46	CreatedBy	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	Y	CreatedBy	2020-10-29 09:52:07.812	0	N	0	\N	\N	Y	N
0	597	\N	0	10	46	Expression	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	N	Expression	2020-10-29 09:52:17.605	0	N	0	\N	\N	Y	N
0	590	\N	0	20	46	IsActive	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	Y	IsActive	2020-10-29 09:52:24.271	0	N	0	\N	\N	Y	N
0	593	\N	0	16	46	Updated	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	Y	Updated	2020-10-29 09:52:29.162	0	N	0	\N	\N	Y	N
0	594	\N	0	11	46	UpdatedBy	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	Y	UpdatedBy	2020-10-29 09:52:36.56	0	N	0	\N	\N	Y	N
0	616	\N	0	19	48	AD_Client_ID	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	Y	AD_Client_ID	2020-12-13 19:50:29.344	0	N	0	\N	\N	Y	N
0	627	\N	0	10	48	AD_JobDefinition_UU	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	N	AD_JobDefinition_UU	2020-12-13 19:50:49.77	0	N	0	\N	\N	Y	N
0	617	\N	0	19	48	AD_Org_ID	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	Y	AD_Org_ID	2020-12-13 19:51:30.476	0	N	0	\N	\N	Y	N
0	626	\N	0	19	48	AD_Process_ID	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	Y	AD_Process_ID	2020-12-13 19:51:38.368	0	N	0	\N	\N	Y	N
0	619	\N	0	16	48	Created	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	Y	Created	2020-12-13 19:51:45.989	0	N	0	\N	\N	Y	N
0	620	\N	0	11	48	CreatedBy	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	Y	CreatedBy	2020-12-13 19:51:54.89	0	N	0	\N	\N	Y	N
0	624	\N	0	10	48	Description	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	N	Description	2020-12-13 19:52:02.116	0	N	0	\N	\N	Y	N
0	625	\N	0	10	48	Help	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	N	Help	2020-12-13 19:52:08.685	0	N	0	\N	\N	Y	N
0	618	\N	0	20	48	IsActive	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	Y	IsActive	2020-12-13 19:52:16.54	0	N	0	\N	\N	Y	N
0	621	\N	0	16	48	Updated	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	Y	Updated	2020-12-13 19:52:27.876	0	N	0	\N	\N	Y	N
0	622	\N	0	11	48	UpdatedBy	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	Y	UpdatedBy	2020-12-13 19:52:33.894	0	N	0	\N	\N	Y	N
0	509	\N	0	10	40	Description	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	Description	2021-01-01 14:01:38.314	0	Y	0	\N	\N	Y	N
0	615	\N	0	13	48	AD_JobDefinition_ID	2020-12-13 18:49:43.704461	0	\N	\N	Y	Y	Y	AD_JobDefinition_ID	2021-03-15 09:27:18.315	0	N	0	\N	\N	Y	Y
0	682	\N	0	19	2	AD_App_ID	2021-02-06 15:33:14.337557	0	\N	\N	Y	N	N	AD_App_ID	2021-02-06 15:33:14.337557	0	N	0	\N	\N	Y	N
0	595	\N	0	19	46	AD_App_ID	2020-10-29 09:48:28.577669	0	\N	\N	Y	N	Y	AD_App_ID	2021-03-15 09:12:13.497	0	N	0	\N	\N	Y	Y
0	584	\N	0	10	45	Name	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	Y	Name	2020-09-14 20:07:18.589	0	N	0	\N	\N	Y	S
0	583	\N	0	10	45	Value	2020-09-14 20:05:49.333815	0	\N	\N	Y	N	Y	Value	2020-09-14 20:07:38.046	0	N	0	\N	\N	Y	S
0	401	\N	0	16	33	Updated	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	Updated	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	N
0	726	\N	0	10	55	ActionName	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	N	ActionName	2021-05-06 20:42:35.921	0	N	0	\N	\N	Y	N
0	727	\N	0	19	55	AD_Tab_ID	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	N	AD_Tab_ID	2021-05-06 20:43:10.533	0	N	0	\N	\N	Y	N
0	729	\N	0	10	55	AD_ToolbarButton_UU	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	N	AD_ToolbarButton_UU	2021-05-06 20:43:40.933	0	N	0	\N	\N	Y	N
0	728	\N	0	20	55	IsLinkedToSelectedRecord	2021-05-06 20:39:33.474519	0	\N	\N	Y	N	Y	IsLinkedToSelectedRecord	2021-05-06 20:44:36.933	0	N	0	\N	\N	Y	N
0	751	\N	0	10	56	ColumnName	2021-05-08 20:24:08.866013	0	\N	\N	Y	N	Y	ColumnName	2021-05-08 20:24:08.866013	0	N	0	\N	\N	Y	Y
0	415	\N	0	19	34	AD_Client_ID	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	Y	AD_Client_ID	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	416	\N	0	19	34	AD_Org_ID	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	Y	AD_Org_ID	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	414	\N	0	19	34	AD_Role_ID	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	Y	AD_Role_ID	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	413	\N	0	19	34	AD_User_ID	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	Y	AD_User_ID	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	423	\N	0	13	34	AD_User_Roles_ID	2020-03-27 18:51:18.070096	0	\N	\N	Y	Y	Y	AD_User_Roles_ID	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	422	\N	0	10	34	AD_User_Roles_UU	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	N	AD_User_Roles_UU	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	418	\N	0	16	34	Created	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	Y	Created	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	419	\N	0	18	34	CreatedBy	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	Y	CreatedBy	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	417	\N	0	20	34	IsActive	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	Y	IsActive	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	420	\N	0	16	34	Updated	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	Y	Updated	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	421	\N	0	18	34	UpdatedBy	2020-03-27 18:51:18.070096	0	\N	\N	Y	N	Y	UpdatedBy	2020-03-27 18:51:18.070096	0	N	0	\N	\N	Y	N
0	452	\N	0	11	2	TokenExpiresIn	2020-04-07 12:48:42.474647	0	\N	\N	Y	N	N	Token Expires In	2020-04-07 12:48:42.474647	0	N	0	\N	\N	Y	N
0	455	\N	0	20	12	IsAccountVerified	2020-04-10 14:01:24.099228	0	\N	\N	Y	N	N	isAccountVerified	2020-04-10 14:01:24.099228	0	N	0	\N	\N	Y	N
0	529	\N	0	19	11	AD_MailConfig_ID	2020-04-16 09:24:35.21759	0	\N	\N	Y	N	N	AD_MailConfig_ID	2020-04-16 09:24:35.21759	0	N	0	\N	\N	Y	N
0	600	\N	0	19	47	AD_Client_ID	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	AD_Client_ID	2020-10-29 09:53:40.404	0	N	0	\N	\N	Y	N
0	601	\N	0	19	47	AD_Org_ID	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	AD_Org_ID	2020-10-29 09:53:46.658	0	N	0	\N	\N	Y	N
0	607	\N	0	19	47	AD_Reference_ID	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	AD_Reference_ID	2020-10-29 09:53:53.717	0	N	0	\N	\N	Y	N
0	599	\N	0	13	47	AD_Variable_ID	2020-10-29 09:52:51.671689	0	\N	\N	Y	Y	Y	AD_Variable_ID	2020-10-29 09:54:00.16	0	N	0	\N	\N	Y	N
0	614	\N	0	10	47	AD_Variable_UU	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	N	AD_Variable_UU	2020-10-29 09:54:06.611	0	N	0	\N	\N	Y	N
0	609	\N	0	10	47	ColumnSQL	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	N	ColumnSQL	2020-10-29 09:54:21.238	0	N	0	\N	\N	Y	N
0	610	\N	0	10	47	ConstantValue	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	N	ConstantValue	2020-10-29 09:54:31.935	0	N	0	\N	\N	Y	N
0	603	\N	0	16	47	Created	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	Created	2020-10-29 09:54:36.867	0	N	0	\N	\N	Y	N
0	604	\N	0	11	47	CreatedBy	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	CreatedBy	2020-10-29 09:54:46.48	0	N	0	\N	\N	Y	N
0	611	\N	0	10	47	Description	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	N	Description	2020-10-29 09:54:51.897	0	N	0	\N	\N	Y	N
0	602	\N	0	20	47	IsActive	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	IsActive	2020-10-29 09:54:58.381	0	N	0	\N	\N	Y	N
0	605	\N	0	16	47	Updated	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	Updated	2020-10-29 09:55:18.141	0	N	0	\N	\N	Y	N
0	606	\N	0	11	47	UpdatedBy	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	UpdatedBy	2020-10-29 09:55:24.964	0	N	0	\N	\N	Y	N
0	612	\N	0	17	47	Type	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	Type	2020-10-29 09:58:32.269	0	N	0	\N	47	Y	N
0	608	\N	0	10	47	Classname	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	N	Classname	2020-10-29 13:53:32.181	0	N	0	\N	\N	Y	N
0	657	\N	0	19	51	AD_Client_ID	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	AD_Client_ID	2021-01-01 14:02:26.991	0	N	0	\N	\N	Y	N
0	663	\N	0	11	51	UpdatedBy	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	UpdatedBy	2021-01-01 14:03:39.812	0	N	0	\N	\N	Y	N
0	683	\N	0	19	3	AD_App_ID	2021-02-06 15:57:43.8717	0	\N	\N	Y	N	N	AD_App_ID	2021-02-06 15:57:43.8717	0	N	0	\N	\N	Y	N
0	628	\N	0	10	48	ProcedureName	2020-12-13 18:54:17.680604	0	\N	\N	Y	N	N	ProcedureName	2021-03-15 09:27:31.375	0	N	0	\N	\N	Y	N
0	613	\N	0	10	47	Value	2020-10-29 09:52:51.671689	0	\N	\N	Y	N	Y	Value	2020-10-29 09:55:31.111	0	N	0	\N	\N	Y	S
0	409	\N	0	18	5	AD_Reference_Value_ID	2020-03-27 10:40:58.553535	0			Y	N	N	AD_Reference_Value_ID	2020-03-27 10:40:58.553535	0	N	0	\N	1	Y	N
0	752	\N	0	19	56	AD_Reference_ID	2021-05-08 21:13:44.825981	0	\N	\N	Y	N	Y	AD_Reference_ID	2021-05-08 21:13:44.825981	0	N	0	\N	2	Y	N
0	425	\N	0	19	35	AD_Client_ID	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	Y	AD_Client_ID	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	426	\N	0	19	35	AD_Org_ID	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	Y	AD_Org_ID	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	424	\N	0	13	35	AD_Role_ID	2020-03-27 18:58:48.238042	0	\N	\N	Y	Y	Y	AD_Role_ID	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	434	\N	0	10	35	AD_Role_UU	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	N	AD_Role_UU	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	428	\N	0	16	35	Created	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	Y	Created	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	429	\N	0	18	35	CreatedBy	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	Y	CreatedBy	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	433	\N	0	10	35	Description	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	N	Description	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	427	\N	0	20	35	IsActive	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	Y	IsActive	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	430	\N	0	16	35	Updated	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	Y	Updated	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	432	\N	0	18	35	UpdatedBy	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	Y	UpdatedBy	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	N
0	630	\N	0	19	49	AD_Client_ID	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	Y	AD_Client_ID	2020-12-13 20:10:50.13	0	N	0	\N	\N	Y	N
0	643	\N	0	10	49	AD_CronJob_UU	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	N	AD_CronJob_UU	2020-12-13 20:11:03.98	0	N	0	\N	\N	Y	N
0	642	\N	0	19	49	AD_JobDefinition_ID	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	Y	AD_JobDefinition_ID	2020-12-13 20:11:24.026	0	N	0	\N	\N	Y	N
0	631	\N	0	19	49	AD_Org_ID	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	Y	AD_Org_ID	2020-12-13 20:11:31.361	0	N	0	\N	\N	Y	N
0	633	\N	0	16	49	Created	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	Y	Created	2020-12-13 20:11:36.167	0	N	0	\N	\N	Y	N
0	634	\N	0	11	49	CreatedBy	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	Y	CreatedBy	2020-12-13 20:11:42.844	0	N	0	\N	\N	Y	N
0	641	\N	0	10	49	CronExpression	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	N	CronExpression	2020-12-13 20:11:49.93	0	N	0	\N	\N	Y	N
0	632	\N	0	20	49	IsActive	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	Y	IsActive	2020-12-13 20:12:02.322	0	N	0	\N	\N	Y	N
0	635	\N	0	16	49	Updated	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	Y	Updated	2020-12-13 20:12:33.643	0	N	0	\N	\N	Y	N
0	636	\N	0	11	49	UpdatedBy	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	Y	UpdatedBy	2020-12-13 20:12:40.367	0	N	0	\N	\N	Y	N
0	637	\N	0	10	49	CurrentStatus	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	Y	CurrentStatus	2020-12-15 19:27:31.707	0	N	0	\N	\N	N	N
0	639	\N	0	16	49	LastEndTime	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	N	LastEndTime	2020-12-15 19:27:36.5	0	N	0	\N	\N	N	N
0	638	\N	0	20	49	LastResult	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	N	LastResult	2020-12-15 19:27:41.014	0	N	0	\N	\N	N	N
0	640	\N	0	16	49	LastStartTime	2020-12-13 19:10:09.462313	0	\N	\N	Y	N	N	LastStartTime	2020-12-15 19:27:44.098	0	N	0	\N	\N	N	N
0	665	\N	0	10	51	Description	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	N	Description	2021-01-01 14:03:05.369	0	N	0	\N	\N	Y	N
0	629	\N	0	13	49	AD_CronJob_ID	2020-12-13 19:10:09.462313	0	\N	\N	Y	Y	Y	AD_CronJob_ID	2021-03-15 09:17:13.088	0	N	0	\N	\N	Y	Y
0	431	\N	0	10	35	Name	2020-03-27 18:58:48.238042	0	\N	\N	Y	N	Y	Name	2020-03-27 18:58:48.238042	0	N	0	\N	\N	Y	S
0	456	\N	0	10	12	Name	2020-04-10 14:35:34.204556	0	\N	\N	Y	N	N	Name	2020-04-10 14:35:34.204556	0	N	0	\N	\N	Y	S
0	84	5fbe18b1-653f-4666-9185-8a3d13b487bb	0	19	5	AD_Reference_ID	2019-12-28 17:53:10.949937	0	System Reference and Validation	\N	Y	N	Y	Reference	2019-12-28 17:53:10.949937	0	N	0	\N	2	Y	N
0	435	\N	0	20	21	IsReadOnly	2020-03-27 19:16:58.837292	0			Y	N	N	IsReadOnly	2020-03-27 19:16:58.837292	0	N	0	\N	\N	Y	N
0	457	\N	0	17	27	ServiceType	2020-04-15 22:42:32.461442	0	\N	\N	Y	N	N	ServiceType	2020-04-15 22:42:32.461442	0	N	0	\N	43	Y	N
0	644	\N	0	19	49	AD_User_ID	2020-12-13 21:18:18.782764	0	\N	\N	Y	N	N	AD_User_ID	2020-12-13 21:18:18.782764	0	N	0	\N	\N	Y	N
0	669	\N	0	20	12	IsViewOnlyActiveRecords	2021-01-17 10:37:21.18944	0	\N	\N	Y	N	N	IsViewOnlyActiveRecords	2021-01-17 10:37:21.18944	0	N	0	\N	\N	Y	N
0	375	\N	0	10	32	AD_Language	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	AD_Language	2021-03-15 09:27:47.098	0	N	0	\N	\N	Y	Y
0	411	\N	0	10	26	Value	2020-03-27 18:17:22.488504	0			Y	N	N	Value	2020-03-27 18:17:22.488504	0	N	0	\N	\N	Y	S
0	194	e9adecff-4fc3-4218-ad80-ac176543eb22	0	10	10	Value	2019-12-31 15:16:08.382783	0	Search key for the record in the format required - must be unique	\N	Y	N	Y	Search Key	2019-12-31 15:16:08.382783	0	N	0	\N	\N	Y	S
0	161	33be3cf7-82b7-4ba7-83aa-6ef18e2ae483	0	10	13	Value	2019-12-31 15:24:13.28336	0	Search key for the record in the format required - must be unique	\N	Y	N	Y	Search Key	2019-12-31 15:24:13.28336	0	N	0	\N	\N	Y	S
0	320	\N	0	10	28	Value	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	Value	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	S
0	437	\N	0	10	21	Value	2020-03-27 19:25:26.983511	0			Y	N	N	Value	2020-03-27 19:25:26.983511	0	N	0	\N	\N	Y	S
0	753	\N	0	18	56	AD_Reference_Value_ID	2021-05-08 21:13:56.796518	0	\N	\N	Y	N	N	AD_Reference_Value_ID	2021-05-08 21:21:42.302	0	N	0	\N	1	Y	N
0	684	\N	0	19	53	AD_Client_ID	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	Y	AD_Client_ID	2021-04-26 18:34:29.342	0	N	0	\N	\N	Y	N
0	685	\N	0	19	53	AD_Org_ID	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	Y	AD_Org_ID	2021-04-26 18:34:35.248	0	N	0	\N	\N	Y	N
0	686	\N	0	13	53	AD_Scripting_ID	2021-04-26 18:33:22.96857	0	\N	\N	Y	Y	Y	AD_Scripting_ID	2021-04-26 19:08:50.221	0	N	0	\N	\N	Y	N
0	693	\N	0	14	53	Content	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	N	Content	2021-04-26 19:08:57.028	0	N	0	\N	\N	Y	N
0	687	\N	0	16	53	Created	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	Y	Created	2021-04-26 19:09:03.993	0	N	0	\N	\N	Y	N
0	688	\N	0	11	53	CreatedBy	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	Y	CreatedBy	2021-04-26 19:09:10.392	0	N	0	\N	\N	Y	N
0	689	\N	0	10	53	Description	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	N	Description	2021-04-26 19:09:16.175	0	N	0	\N	\N	Y	N
0	690	\N	0	20	53	IsActive	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	Y	IsActive	2021-04-26 19:09:22.915	0	N	0	\N	\N	Y	N
0	694	\N	0	16	53	Updated	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	Y	Updated	2021-04-26 19:13:23.601	0	N	0	\N	\N	Y	N
0	695	\N	0	11	53	UpdatedBy	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	Y	UpdatedBy	2021-04-26 19:13:30.033	0	N	0	\N	\N	Y	N
0	696	\N	0	10	53	Value	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	Y	Value	2021-04-28 10:47:28.289	0	N	0	\N	\N	Y	N
0	691	\N	0	10	53	Name	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	Y	Name	2021-04-28 10:47:33.069	0	N	0	\N	\N	Y	Y
0	692	\N	0	17	53	EngineType	2021-04-26 18:33:22.96857	0	\N	\N	Y	N	N	EngineType	2021-04-28 11:55:32.164	0	N	0	\N	48	Y	N
0	178	80ff7a27-7aa5-4d1e-a42f-a6ee4966e258	0	16	15	Updated	2019-12-31 15:43:11.969246	0	Date this record was updated	\N	Y	N	Y	Updated	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	754	\N	0	19	46	AD_Role_ID	2021-05-11 11:11:37.768569	0	\N	\N	Y	N	Y	AD_Role_ID	2021-05-11 11:11:37.768569	0	N	0	\N	\N	Y	Y
0	410	\N	0	19	6	AD_Table_ID	2020-03-27 11:04:02.098701	0			Y	N	N	Table	2020-03-27 11:04:02.098701	0	N	0	\N	\N	Y	N
0	646	\N	0	19	50	AD_Client_ID	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	AD_Client_ID	2020-12-28 14:09:37.046	0	N	0	\N	\N	Y	N
0	648	\N	0	10	50	AD_Language	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	AD_Language	2020-12-28 14:09:47.155	0	N	0	\N	\N	Y	N
0	645	\N	0	19	50	AD_Message_ID	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	AD_Message_ID	2020-12-28 14:09:55.524	0	N	0	\N	\N	Y	N
0	647	\N	0	19	50	AD_Org_ID	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	AD_Org_ID	2020-12-28 14:10:01.468	0	N	0	\N	\N	Y	N
0	650	\N	0	16	50	Created	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	Created	2020-12-28 14:10:08.159	0	N	0	\N	\N	Y	N
0	651	\N	0	11	50	CreatedBy	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	CreatedBy	2020-12-28 14:10:13.509	0	N	0	\N	\N	Y	N
0	649	\N	0	20	50	IsActive	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	IsActive	2020-12-28 14:10:19.352	0	N	0	\N	\N	Y	N
0	654	\N	0	14	50	MsgText	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	MsgText	2020-12-28 14:10:25.808	0	N	0	\N	\N	Y	N
0	655	\N	0	14	50	MsgTip	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	N	MsgTip	2020-12-28 14:10:31.532	0	N	0	\N	\N	Y	N
0	652	\N	0	16	50	Updated	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	Updated	2020-12-28 14:10:38.013	0	N	0	\N	\N	Y	N
0	653	\N	0	11	50	UpdatedBy	2020-12-28 13:08:52.616232	0	\N	\N	Y	N	Y	UpdatedBy	2020-12-28 14:10:43.915	0	N	0	\N	\N	Y	N
0	670	\N	0	20	5	IsIdentifier	2021-01-19 23:17:49.600833	0	The record is active in the system	\N	Y	N	Y	IsIdentifier	2021-01-20 00:17:58.005	0	N	0	\N	\N	Y	N
0	383	\N	0	10	32	Name	2020-03-14 13:39:59.177351	0	\N	\N	Y	N	Y	Name	2020-03-14 13:39:59.177351	0	N	0	\N	\N	Y	S
0	47	dfbe4cc3-8098-4d36-bb56-db0bd9853db2	0	10	5	Name	2019-12-28 17:53:10.892229	0	Alphanumeric identifier of the entity	\N	Y	N	Y	Name	2021-03-15 09:16:21.928	0	N	0	\N	\N	Y	S
0	63	58a5a6ef-0045-4fba-889e-7dd2ed614f4e	0	10	8	Name	2019-12-28 17:53:10.930889	0	Alphanumeric identifier of the entity	\N	Y	N	Y	Name	2019-12-28 17:53:10.930889	0	N	0	\N	\N	Y	S
0	403	\N	0	10	33	Value	2020-03-27 09:43:25.843312	0	\N	\N	Y	N	Y	Value	2020-03-27 09:43:25.843312	0	N	0	\N	\N	Y	S
0	508	\N	0	10	40	Name	2020-04-16 08:56:34.354575	0	\N	\N	Y	N	Y	Name	2021-01-01 14:27:12.11	0	N	0	\N	\N	Y	S
0	173	3087ab42-b72b-44ad-bdbb-8c2360c48d39	0	19	15	AD_Client_ID	2019-12-31 15:43:11.969246	0	Client/Tenant for this installation.	\N	Y	N	Y	AD_Client_ID	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	174	1253bda8-d45f-45e0-8e39-2da98543f119	0	19	15	AD_Org_ID	2019-12-31 15:43:11.969246	0	Organizational entity within client	\N	Y	N	Y	AD_Org_ID	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	623	\N	0	10	48	Name	2020-12-13 18:49:43.704461	0	\N	\N	Y	N	Y	Name	2021-03-15 09:27:28.276	0	N	0	\N	\N	Y	S
0	436	\N	0	10	1	Name	2020-03-27 19:23:45.657423	0			Y	N	N	Name	2020-03-27 19:23:45.657423	0	N	0	\N	\N	Y	S
0	170	b845bb71-bef6-47bd-835b-57e0e78ee0b0	0	10	14	Name	2019-12-31 15:30:55.741282	0	Alphanumeric identifier of the entity	\N	Y	N	Y	Name	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	S
0	172	70dd5086-2f43-4099-90fb-78df58cf2e60	0	10	15	Name	2019-12-31 19:12:39.931528	0		\N	Y	N	Y	Name	2019-12-31 19:12:39.931528	100	N	0	\N	\N	Y	S
0	697	\N	0	19	48	AD_Scripting_ID	2021-04-28 10:47:00.393384	0	\N	\N	Y	N	N	AD_Scripting_ID	2021-04-28 10:47:00.393384	0	N	0	\N	\N	Y	N
0	171	eb8c9122-cca3-4694-adf3-ac171e26e9fa	0	10	14	Description	2019-12-31 15:30:55.741282	0	Optional short description of the record	\N	Y	N	N	Description	2019-12-31 15:30:55.741282	0	N	0	\N	\N	Y	N
0	201	2e658efa-6ff9-4df6-9a39-99d479faaa41	0	20	14	IsDefault	2019-12-31 15:30:55.741282	100	Default value	\N	Y	N	Y	Default	2019-12-31 15:30:55.741282	100	N	0	\N	\N	Y	N
0	175	4b250bcc-4d54-4bf2-a6b2-85e370096a9d	0	20	15	IsActive	2019-12-31 15:43:11.969246	0	The record is active in the system	\N	Y	N	Y	Active	2019-12-31 15:43:11.969246	0	N	0	\N	\N	Y	N
0	289	\N	0	20	4	isTranslated	2020-02-26 15:43:15.529641	0			Y	N	N	Translated	2020-02-26 15:43:15.529641	0	N	0	\N	\N	Y	N
0	368	292ba7d1-3943-4a68-bd6c-870305e48cf8	0	20	15	IsSummary	2019-12-31 15:43:11.969246	100	Default value	\N	Y	N	Y	Is Summary	2019-12-31 15:43:11.969246	100	N	0	\N	\N	Y	N
0	285	\N	0	10	26	ModelProviderClass	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	N	Model Provider Class Name	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	78	69ab63bd-e88d-4691-9e2b-98cba90be941	0	10	10	MsgText	2019-12-31 15:16:08.382783	0	Textual Informational, Menu or Error Message	\N	Y	N	Y	Message Text	2019-12-31 15:16:08.382783	0	Y	0	\N	\N	Y	N
0	288	\N	0	20	5	isTranslatable	2020-02-26 15:42:24.665731	0			Y	N	N	Translatable	2020-02-26 15:42:24.665731	0	N	0	\N	\N	Y	N
0	292	\N	0	19	1	AD_Extension_ID	2020-02-27 22:25:19.6561	0			Y	N	N	AD_Extension_ID	2020-02-27 22:25:19.6561	0	N	0	\N	\N	Y	N
0	293	\N	0	19	5	AD_Extension_ID	2020-02-27 22:25:35.093629	0			Y	N	N	AD_Extension_ID	2020-02-27 22:25:35.093629	0	N	0	\N	\N	Y	N
0	294	\N	0	19	7	AD_Extension_ID	2020-02-27 22:25:47.517964	0			Y	N	N	AD_Extension_ID	2020-02-27 22:25:47.517964	0	N	0	\N	\N	Y	N
0	295	\N	0	19	8	AD_Extension_ID	2020-02-27 22:26:00.807529	0			Y	N	N	AD_Extension_ID	2020-02-27 22:26:00.807529	0	N	0	\N	\N	Y	N
0	296	\N	0	19	9	AD_Extension_ID	2020-02-27 22:26:13.026825	0			Y	N	N	AD_Extension_ID	2020-02-27 22:26:13.026825	0	N	0	\N	\N	Y	N
0	297	\N	0	19	24	AD_Extension_ID	2020-02-27 22:27:18.628174	0			Y	N	N	AD_Extension_ID	2020-02-27 22:27:18.628174	0	N	0	\N	\N	Y	N
0	298	\N	0	19	6	AD_Extension_ID	2020-02-27 22:27:30.833148	0			Y	N	N	AD_Extension_ID	2020-02-27 22:27:30.833148	0	N	0	\N	\N	Y	N
0	299	\N	0	19	4	AD_Extension_ID	2020-02-27 22:27:46.599633	0			Y	N	N	AD_Extension_ID	2020-02-27 22:27:46.599633	0	N	0	\N	\N	Y	N
0	309	\N	0	10	27	Attributes	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	N	Attributes	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	301	\N	0	19	27	AD_Client_ID	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	Y	AD_Client_ID	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	302	\N	0	19	27	AD_Org_ID	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	Y	AD_Org_ID	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	303	\N	0	20	27	IsActive	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	Y	IsActive	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	304	\N	0	16	27	Created	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	Y	Created	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	306	\N	0	16	27	Updated	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	Y	Updated	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	307	\N	0	18	27	UpdatedBy	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	Y	UpdatedBy	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	339	\N	0	10	30	Attributes	2020-03-07 19:54:28.961542	0	\N	\N	Y	N	N	Attributes	2020-03-07 19:54:28.961542	0	N	0	\N	\N	Y	N
0	311	\N	0	19	27	AD_Extension_ID	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	N	AD_Extension_ID	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	314	\N	0	19	28	AD_Org_ID	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	AD_Org_ID	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	315	\N	0	20	28	IsActive	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	IsActive	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	316	\N	0	16	28	Created	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	Created	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	317	\N	0	18	28	CreatedBy	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	CreatedBy	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	318	\N	0	16	28	Updated	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	Updated	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	319	\N	0	18	28	UpdatedBy	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	UpdatedBy	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	321	\N	0	19	28	AD_MediaFormat_ID	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	AD_MediaFormat_ID	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	322	\N	0	19	28	AD_MediaFolder_ID	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	Y	AD_MediaFolder_ID	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	323	\N	0	10	28	AD_Media_UU	2020-03-07 19:50:02.359401	0	\N	\N	Y	N	N	AD_Media_UU	2020-03-07 19:50:02.359401	0	N	0	\N	\N	Y	N
0	325	\N	0	19	29	AD_Client_ID	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	Y	AD_Client_ID	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	326	\N	0	19	29	AD_Org_ID	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	Y	AD_Org_ID	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	327	\N	0	20	29	IsActive	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	Y	IsActive	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	328	\N	0	16	29	Created	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	Y	Created	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	329	\N	0	18	29	CreatedBy	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	Y	CreatedBy	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	330	\N	0	16	29	Updated	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	Y	Updated	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	331	\N	0	18	29	UpdatedBy	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	Y	UpdatedBy	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	332	\N	0	10	29	Extension	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	Y	Extension	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	333	\N	0	10	29	MimeType	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	Y	MimeType	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	334	\N	0	10	29	Description	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	N	Description	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	335	\N	0	10	29	AD_MediaFormat_UU	2020-03-07 19:54:12.906447	0	\N	\N	Y	N	N	AD_MediaFormat_UU	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	340	\N	0	10	30	AD_MediaFolder_UU	2020-03-07 19:54:28.961542	0	\N	\N	Y	N	N	AD_MediaFolder_UU	2020-03-07 19:54:28.961542	0	N	0	\N	\N	Y	N
0	341	\N	0	20	30	IsInternalStorage	2020-03-07 19:54:28.961542	0	Internal Storage: All the media file (bytes) will be returned by the app / External Storage: the costumer will be redirect for external provider	\N	Y	N	Y	IsInternalStorage	2020-03-07 19:54:28.961542	0	N	0	\N	\N	Y	N
0	342	\N	0	20	30	IsSecurityAccess	2020-03-07 19:54:28.961542	0	Evaluate whether is necessary to have a special token to get the file or not	\N	Y	N	Y	IsSecurityAccess	2020-03-07 19:54:28.961542	0	N	0	\N	\N	Y	N
0	287	\N	0	10	26	AD_Extension_UU	2020-02-23 01:22:37.244846	0	\N	\N	Y	N	N	AD_Extensions_UU	2020-02-23 01:22:37.244846	0	N	0	\N	\N	Y	N
0	77	f2abbbcc-a4e9-4710-a3a2-338479ad484f	0	17	10	MsgType	2019-12-31 15:16:08.382783	0	Type of message (Informational, Menu or Error)	\N	Y	N	Y	Message Type	2019-12-31 15:16:08.382783	0	N	0	\N	41	Y	N
0	300	\N	0	13	27	AD_ServiceProvider_ID	2020-02-28 21:05:19.724712	0	\N	\N	Y	Y	Y	AD_ServiceProvider_ID	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	336	\N	0	13	30	AD_MediaFolder_ID	2020-03-07 19:54:28.961542	0	\N	\N	Y	Y	Y	AD_MediaFolder_ID	2020-03-07 19:54:28.961542	0	N	0	\N	\N	Y	N
0	324	\N	0	13	29	AD_MediaFormat_ID	2020-03-07 19:54:12.906447	0	\N	\N	Y	Y	Y	AD_MediaFormat_ID	2020-03-07 19:54:12.906447	0	N	0	\N	\N	Y	N
0	310	\N	0	10	27	AD_ServiceProvider_UU	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	N	AD_ServiceProvider_UU	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	N
0	656	\N	0	19	51	AD_NotificationTemplate_ID	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	AD_NotificationTemplate_ID	2021-01-01 14:02:44.109	0	N	0	\N	\N	Y	N
0	658	\N	0	19	51	AD_Org_ID	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	AD_Org_ID	2021-01-01 14:02:49.9	0	N	0	\N	\N	Y	N
0	660	\N	0	16	51	Created	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	Created	2021-01-01 14:02:54.944	0	N	0	\N	\N	Y	N
0	661	\N	0	11	51	CreatedBy	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	CreatedBy	2021-01-01 14:03:00.233	0	N	0	\N	\N	Y	N
0	659	\N	0	20	51	IsActive	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	IsActive	2021-01-01 14:03:17.698	0	N	0	\N	\N	Y	N
0	662	\N	0	16	51	Updated	2021-01-01 13:00:54.930254	0	\N	\N	Y	N	Y	Updated	2021-01-01 14:03:34.107	0	N	0	\N	\N	Y	N
0	308	\N	0	10	27	Value	2020-02-28 21:05:19.724712	0	\N	\N	Y	N	Y	Value	2020-02-28 21:05:19.724712	0	N	0	\N	\N	Y	S
0	337	\N	0	10	30	Name	2020-03-07 19:54:28.961542	0	\N	\N	Y	N	Y	Name	2021-03-15 09:36:12.803	0	N	0	\N	\N	Y	S
\.


--
-- Data for Name: ad_cronjob; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_cronjob (ad_cronjob_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, lastresult, lastendtime, laststarttime, cronexpression, ad_jobdefinition_id, ad_cronjob_uu, ad_user_id, currentstatus) FROM stdin;
1	0	0	N	2021-04-28 12:29:12.856344	0	2021-04-28 12:37:15.493	0	\N	2021-04-28 12:37:09.74	2021-04-28 12:37:00.651	0 * * * * *	7	a4504c94-cf2a-4a66-a40d-22ba32bf2b58	0	ERROR
\.


--
-- Data for Name: ad_extension; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_extension (ad_extension_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, modelproviderclass, ad_extension_uu, seqno, serviceproviderclass, value) FROM stdin;
0	0	0	Y	2020-03-08 02:25:35.417223	0	2020-03-08 02:25:35.417223	0	Cadre	Cadre extension	com.cadre.server.core.entity.DefaultModelProvider	55d1180d-f4d0-4569-af05-b09969c7e2ea	0	\N	cadre-core
\.


--
-- Data for Name: ad_field; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_field (ad_client_id, ad_field_id, ad_field_uu, ad_org_id, ad_tab_id, ad_column_id, created, createdby, defaultvalue, description, help, isactive, isdisplayed, isdisplayedgrid, ismandatory, isreadonly, issameline, label, placeholder, seqno, updated, updatedby, bootstrapclass, ad_extension_id, dynamicvalidation) FROM stdin;
0	262	\N	0	31	313	2020-03-27 20:20:28.554823	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 20:20:28.554823	0	col-md-6 mb-3	0	\N
0	263	\N	0	31	314	2020-03-27 20:20:39.451155	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 20:20:39.451155	0	col-md-6 mb-3	0	\N
0	154	\N	0	13	298	2020-03-27 13:59:48.347215	0	\N			Y	Y	Y	Y	N	Y	Extension		35	2020-03-27 13:59:48.347215	0	col-md-6 mb-3	0	\N
0	137	\N	0	13	54	2020-03-27 09:59:52.746165	0	\N			Y	Y	Y	Y	N	N	Name		30	2020-03-27 09:59:52.746165	0	col-md-6 mb-3	0	\N
0	63	e42e9360-3283-4eca-8425-558c5a055224	0	3	84	2019-12-30 21:10:46.366493	0		System Reference and Validation	\N	Y	Y	Y	Y	N	N	Reference		80	2019-12-30 21:10:46.366493	0	col-md-6 mb-3	0	ValidationType eq 'D'
0	128	\N	0	2	299	2020-03-25 18:00:18.07748	0				Y	Y	Y	Y	N	Y	Extension		35	2020-03-25 18:00:18.07748	0	col-md-6 mb-3	0	\N
0	132	\N	0	5	295	2020-03-25 20:00:31.313925	0	\N			Y	Y	Y	Y	N	Y	Extension		35	2020-03-25 20:00:31.313925	0	col-md-6 mb-3	0	\N
0	64	07b836f9-a1bd-44d2-a2db-875f048982da	0	3	88	2019-12-30 21:10:46.386717	0		Client/Tenant for this installation.	\N	Y	Y	N	Y	N	N	Client		10	2019-12-30 21:10:46.386717	0	col-md-6 mb-3	0	\N
0	139	\N	0	13	56	2020-03-27 10:03:57.874591	0	\N			Y	Y	Y	N	N	N	Comment/Help		50	2020-03-27 10:03:57.874591	0	col-md-6 mb-3	0	\N
0	141	\N	0	13	206	2020-03-27 10:05:18.480575	0	\N			Y	Y	Y	N	N	N	Order By Value		70	2020-03-27 10:05:18.480575	0	col-md-6 mb-3	0	\N
0	281	\N	0	32	510	2020-04-16 09:10:08.098008	0	N	\N	\N	Y	Y	Y	Y	N	N	Parse Template	\N	60	2020-04-16 09:10:08.098008	0	col-md-6 mb-3	0	\N
0	165	\N	0	16	100	2020-03-27 17:34:13.101672	0	@#AD_Org_ID@			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 17:34:13.101672	0	col-md-6 mb-3	0	\N
0	146	\N	0	14	404	2020-03-27 10:15:53.214996	0	\N			Y	Y	Y	N	N	N	Name		60	2020-03-27 10:15:53.214996	0	col-md-12 mb-3	0	\N
0	164	\N	0	16	80	2020-03-27 17:33:50.637154	0	@#AD_Org_ID@			Y	Y	Y	Y	N	N	Client		10	2020-03-27 17:33:50.637154	0	col-md-6 mb-3	0	\N
0	175	\N	0	18	377	2020-03-27 17:51:41.03825	0	env=@#AD_Org_ID@			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 17:51:41.03825	0	col-md-6 mb-3	0	\N
0	152	\N	0	13	410	2020-03-27 11:04:46.305767	0	\N			Y	Y	Y	N	N	Y	Table		65	2020-03-27 11:04:46.305767	0	col-md-6 mb-3	0	\N
0	264	\N	0	31	320	2020-03-27 20:21:21.390816	0	\N			Y	Y	Y	Y	N	N	File Name		50	2020-03-27 20:21:21.390816	0	col-md-12 mb-3	0	\N
0	176	\N	0	18	375	2020-03-27 17:52:12.908208	0	\N			Y	Y	Y	Y	N	N	Language		30	2020-03-27 17:52:12.908208	0	col-md-6 mb-3	0	\N
0	178	\N	0	18	384	2020-03-27 17:53:01.194976	0	\N			Y	Y	Y	N	N	N	ISO Language Code		50	2020-03-27 17:53:01.194976	0	col-md-6 mb-3	0	\N
0	179	\N	0	18	385	2020-03-27 17:53:18.106477	0	\N			Y	Y	Y	N	N	Y	ISO Country Code		60	2020-03-27 17:53:18.106477	0	col-md-6 mb-3	0	\N
0	181	\N	0	18	386	2020-03-27 17:54:07.349955	0	N			Y	Y	Y	N	N	N	Base Language		80	2020-03-27 17:54:07.349955	0	col-md-6 mb-3	0	\N
0	182	\N	0	18	387	2020-03-27 17:54:31.779453	0	N			Y	Y	Y	N	N	Y	System Language		90	2020-03-27 17:54:31.779453	0	col-md-6 mb-3	0	\N
0	186	\N	0	19	301	2020-03-27 18:00:04.163197	0	@env=@#AD_Client_ID@			Y	Y	Y	Y	N	N	Client		10	2020-03-27 18:00:04.163197	0	col-md-6 mb-3	0	\N
0	191	\N	0	20	276	2020-03-27 18:13:51.582303	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 18:13:51.582303	0	col-md-6 mb-3	0	\N
0	65	656d4754-6a04-47d0-a5a0-397b40b4f688	0	3	89	2019-12-30 21:10:46.431719	0	@AD_Org_ID@	Organizational entity within client	\N	Y	Y	N	Y	N	Y	Organization		20	2019-12-30 21:10:46.431719	0	col-md-6 mb-3	0	\N
0	229	\N	0	25	229	2020-03-27 19:18:02.801653	0	Y			Y	Y	Y	N	N	N	Active		70	2020-03-27 19:18:02.801653	0	col-md-6 mb-3	0	\N
0	199	\N	0	21	102	2020-03-27 18:26:13.520548	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 18:26:13.520548	0	col-md-6 mb-3	0	\N
0	192	\N	0	20	282	2020-03-27 18:15:13.98807	0	\N			Y	Y	Y	N	N	N	Name		40	2020-03-27 18:15:13.98807	0	col-md-6 mb-3	0	\N
0	211	\N	0	22	416	2020-03-27 18:55:53.258058	0	@#AD_Org_ID@			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 18:55:53.258058	0	col-md-6 mb-3	0	\N
0	210	\N	0	22	415	2020-03-27 18:55:08.305435	0	@#AD_Client_ID@			Y	Y	Y	Y	N	N	Client		10	2020-03-27 18:55:08.305435	0	col-md-6 mb-3	0	\N
0	215	\N	0	23	426	2020-03-27 19:08:35.264691	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 19:08:35.264691	0	col-md-6 mb-3	0	\N
0	218	\N	0	24	415	2020-03-27 19:11:36.484546	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 19:11:36.484546	0	col-md-6 mb-3	0	\N
0	221	\N	0	24	413	2020-03-27 19:12:18.308954	0	\N			Y	Y	Y	Y	N	Y	User		40	2020-03-27 19:12:18.308954	0	col-md-6 mb-3	0	\N
0	222	\N	0	24	417	2020-03-27 19:12:36.74497	0	Y			Y	Y	Y	N	N	N	Active		50	2020-03-27 19:12:36.74497	0	col-md-6 mb-3	0	\N
0	226	\N	0	25	239	2020-03-27 19:15:24.519221	0	\N			Y	Y	Y	Y	Y	N	Role		30	2020-03-27 19:15:24.519221	0	col-md-6 mb-3	0	\N
0	239	\N	0	27	441	2020-03-27 19:54:45.788281	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 19:54:45.788281	0	col-md-6 mb-3	0	\N
0	241	\N	0	27	439	2020-03-27 19:55:38.500608	0	\N			Y	Y	Y	Y	N	N	Role		40	2020-03-27 19:55:38.500608	0	col-md-6 mb-3	0	\N
0	243	\N	0	28	440	2020-03-27 19:59:58.209194	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 19:59:58.209194	0	col-md-6 mb-3	0	\N
0	250	\N	0	29	332	2020-03-27 20:07:38.245375	0	\N			Y	Y	Y	Y	N	N	Extension		30	2020-03-27 20:07:38.245375	0	col-md-6 mb-3	0	\N
0	271	\N	0	19	454	2020-04-10 13:03:00.996944	0	\N	\N	\N	Y	Y	Y	Y	N	N	Classname	\N	45	2020-04-10 13:03:00.996944	0	col-md-6 mb-3	0	\N
0	276	\N	0	32	501	2020-04-16 09:07:49.140311	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	N	N	Client	\N	10	2020-04-16 09:07:49.140311	0	col-md-6 mb-3	0	\N
0	282	\N	0	32	503	2020-04-16 09:10:34.516929	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	70	2020-04-16 09:10:34.516929	0	col-md-6 mb-3	0	\N
0	167	\N	0	16	82	2020-03-27 17:35:16.7716	0	\N			Y	Y	Y	N	N	N	Description		40	2020-03-27 17:35:16.7716	0	col-md-12 mb-3	0	\N
0	284	\N	0	33	515	2020-04-16 09:27:17.647364	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	N	N	Client	\N	10	2020-04-16 09:27:17.647364	0	col-md-6 mb-3	0	\N
0	295	\N	0	11	201	2020-04-16 09:32:26.804864	0	\N	\N	\N	Y	Y	Y	N	N	N	Default	\N	50	2020-04-16 09:32:26.804864	0	col-md-6 mb-3	0	\N
0	290	\N	0	33	525	2020-04-16 09:30:03.334742	0	\N	\N	\N	Y	Y	N	Y	N	Y	Request User Password	\N	70	2020-04-16 09:30:03.334742	0	col-md-6 mb-3	0	\N
0	304	\N	0	12	216	2020-04-16 09:52:59.989598	0	\N	\N	\N	Y	Y	Y	N	N	N	Window	\N	80	2020-04-16 09:52:59.989598	0	col-md-6 mb-3	0	\N
0	278	\N	0	32	508	2020-04-16 09:08:40.097484	0	\N	\N	\N	Y	Y	Y	Y	N	N	Name	\N	30	2020-04-16 09:08:40.097484	0	col-md-6 mb-3	0	\N
0	277	\N	0	32	502	2020-04-16 09:08:10.983137	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-04-16 09:08:10.983137	0	col-md-6 mb-3	0	\N
0	308	\N	0	33	530	2020-04-16 11:29:01.737498	0	\N	\N	\N	Y	Y	Y	N	N	N	Name	\N	25	2020-04-16 11:29:01.737498	0	col-md-12 mb-3	0	\N
0	201	\N	0	21	204	2020-03-27 18:27:23.919437	0	\N			Y	Y	Y	Y	N	Y	Password		45	2020-03-27 18:27:23.919437	0	col-md-6 mb-3	0	\N
0	311	\N	0	2	533	2020-04-19 16:37:10.884716	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Data Access Level	\N	47	2020-04-19 16:37:10.884716	0	col-md-6 mb-3	0	\N
0	273	\N	0	21	455	2020-04-10 14:02:27.852676	0	N	\N	\N	Y	Y	Y	Y	N	Y	Account Verified	\N	60	2020-04-10 14:02:27.852676	0	col-md-6 mb-3	0	\N
0	314	\N	0	26	536	2020-04-23 13:53:28.753005	0	\N	\N	\N	Y	Y	Y	N	N	N	Refresh Expires In (milliseconds)	\N	50	2020-04-23 13:53:28.753005	0	col-md-6 mb-3	0	\N
0	318	\N	0	34	555	2020-06-15 17:55:21.524924	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-06-15 17:55:21.524924	0	col-md-6 mb-3	0	\N
0	156	\N	0	15	259	2020-03-27 17:25:56.762106	0	@#AD_Org_ID@			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 17:25:56.762106	0	col-md-6 mb-3	0	\N
0	155	\N	0	15	257	2020-03-27 17:25:40.776804	0	@#AD_Client_ID@			Y	Y	Y	Y	N	N	Client		10	2020-03-27 17:25:40.776804	0	col-md-6 mb-3	0	\N
0	129	\N	0	3	293	2020-03-25 18:03:38.188942	0	\N			Y	Y	Y	Y	N	Y	Extension		35	2020-03-25 18:03:38.188942	0	col-md-6 mb-3	0	\N
0	133	\N	0	6	296	2020-03-25 20:04:22.05801	0	\N			Y	Y	Y	Y	N	Y	Extension		47	2020-03-25 20:04:22.05801	0	col-md-6 mb-3	0	\N
0	138	\N	0	13	55	2020-03-27 10:03:39.918637	0	\N			Y	Y	Y	N	N	N	Description		40	2020-03-27 10:03:39.918637	0	col-md-12 mb-3	0	\N
0	140	\N	0	13	57	2020-03-27 10:04:41.930067	0	\N			Y	Y	Y	N	N	N	Validation Type		60	2020-03-27 10:04:41.930067	0	col-md-6 mb-3	0	\N
0	66	0c0a9614-1212-49ea-8fc8-9b71ed18e63d	0	3	47	2019-12-30 21:10:46.339687	0		Alphanumeric identifier of the entity	\N	Y	Y	Y	Y	N	Y	Name		50	2019-12-30 21:10:46.339687	0	col-md-6 mb-3	0	\N
0	67	4ad31fc9-458d-4d6d-98cf-d438b261cb22	0	3	48	2019-12-30 21:10:46.346887	0		Optional short description of the record	\N	Y	Y	Y	N	N	N	Description		60	2019-12-30 21:10:46.346887	0	col-md-6 mb-3	0	\N
0	147	\N	0	14	405	2020-03-27 10:16:18.779689	0	\N			Y	Y	Y	N	N	N	Description		70	2020-03-27 10:16:18.779689	0	col-md-12 mb-3	0	\N
0	68	0429c789-8da6-45fc-be88-ecc26664efab	0	3	49	2019-12-30 21:10:46.352128	0		Comment or Hint	\N	Y	Y	Y	N	N	N	Comment/Help		70	2019-12-30 21:10:46.352128	0	col-md-6 mb-3	0	\N
0	70	6bbf25c3-a167-4e37-ad08-947c8428b961	0	3	51	2019-12-30 21:10:46.357045	0		Name of the column in the database	\N	Y	Y	Y	Y	N	N	DB Column Name		40	2019-12-30 21:10:46.357045	0	col-md-6 mb-3	0	\N
0	69	b03ce56b-2853-4732-a490-40ab88f62734	0	3	50	2019-12-30 21:10:46.361701	0		Database Table information	\N	Y	Y	Y	Y	Y	N	Table		30	2019-12-30 21:10:46.361701	0	col-md-6 mb-3	0	\N
0	73	e9bf8118-1130-42f4-9a98-68b410653b6c	0	7	77	2019-12-30 20:53:28.292931	0	I	Type of message (Informational, Menu or Error)	\N	Y	Y	Y	Y	N	N	Message Type		60	2019-12-30 20:53:28.292931	0	col-md-6 mb-3	0	\N
0	74	4615dca7-3b28-4364-ba21-3e20a5d4e7dc	0	7	78	2019-12-30 21:10:46.224749	0		Textual Informational, Menu or Error Message	\N	Y	Y	Y	Y	N	N	Message Text		70	2019-12-30 21:10:46.224749	0	col-md-12 mb-3	0	\N
0	72	a1ee934c-5591-425e-9535-ffb1f891c124	0	3	112	2019-12-30 21:10:46.37655	0	Y	The record is active in the system	\N	Y	Y	Y	Y	N	N	Active		120	2019-12-30 21:10:46.37655	0	col-md-6 mb-3	0	\N
0	197	\N	0	20	411	2020-03-27 18:17:46.594235	0	\N			Y	Y	Y	Y	N	N	Value		30	2020-03-27 18:17:46.594235	0	col-md-12 mb-3	0	\N
0	171	\N	0	17	161	2020-03-27 17:45:10.927236	0	\N			Y	Y	Y	Y	N	N	Identifier		30	2020-03-27 17:45:10.927236	0	col-md-12 mb-3	0	\N
0	76	85e711d7-be24-4094-863d-50bbe293a1c9	0	4	59	2019-12-30 21:10:46.241024	0		Alphanumeric identifier of the entity	\N	Y	Y	Y	Y	N	N	Name		30	2019-12-30 21:10:46.241024	0	col-md-6 mb-3	0	\N
0	77	ddac7d4b-5413-4288-9a12-283e9b2382dd	0	4	60	2019-12-30 21:10:46.244984	0		Optional short description of the record	\N	Y	Y	Y	N	N	N	Description		40	2019-12-30 21:10:46.244984	0	col-md-6 mb-3	0	\N
0	78	cc5b2626-983e-41dd-abaa-4fc938bf8caa	0	4	61	2019-12-30 21:10:46.248534	0		Comment or Hint	\N	Y	Y	Y	N	N	N	Comment/Help		50	2019-12-30 21:10:46.248534	0	col-md-6 mb-3	0	\N
0	79	a556aeec-af37-42dc-a73c-525de29b3b46	0	5	66	2019-12-30 21:10:46.251969	0		Data entry or display window	\N	Y	Y	Y	Y	Y	N	Window		30	2019-12-30 21:10:46.251969	0	col-md-6 mb-3	0	\N
0	80	1b245841-7baa-45a3-9215-d919b6884a28	0	5	63	2019-12-30 21:10:46.255267	0		Alphanumeric identifier of the entity	\N	Y	Y	Y	Y	N	N	Name		40	2019-12-30 21:10:46.255267	0	col-md-6 mb-3	0	\N
0	81	3c8a0af6-f5fc-45ee-b396-305c850705a6	0	5	64	2019-12-30 21:10:46.258395	0		Optional short description of the record	\N	Y	Y	Y	N	N	N	Description		50	2019-12-30 21:10:46.258395	0	col-md-6 mb-3	0	\N
0	84	1de3194c-ae09-4118-a768-2a5779aff5a9	0	6	72	2019-12-30 21:10:46.269574	0		Tab within a Window	\N	Y	Y	Y	Y	Y	N	Tab		30	2019-12-30 21:10:46.269574	0	col-md-6 mb-3	0	\N
0	148	\N	0	14	398	2020-03-27 10:16:35.116281	0	Y			Y	Y	Y	Y	N	N	Active		80	2020-03-27 10:16:35.116281	0	col-md-6 mb-3	0	\N
0	75	965c09f6-cec8-4c41-bf00-0a6dc9de0ece	0	7	79	2019-12-30 21:10:46.237078	0		Additional tip or help for this message	\N	Y	Y	Y	N	N	N	Message Tip		90	2019-12-30 21:10:46.237078	0	col-md-12 mb-3	0	\N
0	172	\N	0	17	104	2020-03-27 17:45:33.301531	0	\N			Y	Y	Y	N	N	N	Description		50	2020-03-27 17:45:33.301531	0	col-md-12 mb-3	0	\N
0	153	\N	0	7	99	2020-03-27 13:57:46.596531	0	@#AD_Org_ID@			Y	Y	Y	Y	N	Y	Organization		20	2020-12-28 14:24:29.156	0	col-md-6 mb-3	0	\N
0	163	\N	0	15	264	2020-03-27 17:29:30.125718	0	Y			Y	Y	Y	N	N	Y	Active		90	2020-03-27 17:29:30.125718	0	col-md-6 mb-3	0	\N
0	183	\N	0	18	390	2020-03-27 17:54:48.949371	0	\N			Y	Y	Y	N	N	N	Date Pattern		100	2020-03-27 17:54:48.949371	0	col-md-6 mb-3	0	\N
0	159	\N	0	15	269	2020-03-27 17:27:49.003226	0	\N			Y	Y	Y	Y	N	N	Model Validation Class		45	2020-03-27 17:27:49.003226	0	col-md-12 mb-3	0	\N
0	161	\N	0	15	267	2020-03-27 17:28:42.675622	0	\N			Y	Y	Y	N	N	N	Help		70	2020-03-27 17:28:42.675622	0	col-md-12 mb-3	0	\N
0	126	\N	0	6	217	2020-03-24 20:08:37.288415	0	col-md-6 mb-3			Y	Y	Y	N	N	Y	Class		80	2020-03-24 20:08:37.288415	0	col-md-6 mb-3	0	\N
0	200	\N	0	21	198	2020-03-27 18:26:52.277634	0	\N			Y	Y	Y	Y	N	N	Email Address		30	2020-03-27 18:26:52.277634	0	col-md-6 mb-3	0	\N
0	187	\N	0	19	302	2020-03-27 18:01:35.938651	0				Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 18:01:35.938651	0	col-md-6 mb-3	0	\N
0	193	\N	0	20	283	2020-03-27 18:15:39.913907	0	\N			Y	Y	Y	N	N	N	Description		50	2020-03-27 18:15:39.913907	0	col-md-12 mb-3	0	\N
0	202	\N	0	21	213	2020-03-27 18:28:05.622303	0	N			Y	Y	Y	N	N	N	Locked		50	2020-03-27 18:28:05.622303	0	col-md-6 mb-3	0	\N
0	209	\N	0	3	52	2020-03-27 18:36:31.674493	0	N			Y	Y	Y	N	N	N	Key		95	2020-03-27 18:36:31.674493	0	col-md-6 mb-3	0	\N
0	168	\N	0	16	191	2020-03-27 17:35:48.147093	0	\N			Y	Y	Y	Y	N	Y	Language		35	2020-03-27 17:35:48.147093	0	col-md-6 mb-3	0	\N
0	71	\N	0	6	205	2020-01-19 13:32:21.918814	0	\N	Default Value for field		Y	Y	Y	N	N	Y	Default Value		65	2020-01-19 13:32:21.918814	0	col-md-6 mb-3	0	\N
0	216	\N	0	23	431	2020-03-27 19:08:49.98204	0	\N			Y	Y	Y	N	N	N	Name		30	2020-03-27 19:08:49.98204	0	col-md-6 mb-3	0	\N
0	212	\N	0	22	413	2020-03-27 18:56:39.365083	0	\N			Y	Y	Y	Y	Y	N	User		30	2020-03-27 18:56:39.365083	0	col-md-6 mb-3	0	\N
0	227	\N	0	24	417	2020-03-27 19:16:06.428181	0	Y			Y	Y	Y	N	N	N	Active		50	2020-03-27 19:16:06.428181	0	col-md-6 mb-3	0	\N
0	242	\N	0	27	442	2020-03-27 19:55:51.476951	0	Y			Y	Y	Y	N	N	N	Active		50	2020-03-27 19:55:51.476951	0	col-md-6 mb-3	0	\N
0	162	\N	0	15	270	2020-03-27 17:28:59.987334	0				Y	Y	Y	N	N	N	Sequence		80	2020-08-08 21:19:02.762	0	col-md-6 mb-3	0	\N
0	245	\N	0	28	439	2020-03-27 20:00:27.961282	0	\N			Y	Y	Y	Y	Y	N	Role		30	2020-03-27 20:00:27.961282	0	col-md-6 mb-3	0	\N
0	253	\N	0	29	327	2020-03-27 20:08:32.656011	0	Y			Y	Y	Y	N	N	N	Active		60	2020-03-27 20:08:32.656011	0	col-md-6 mb-3	0	\N
0	228	\N	0	25	435	2020-03-27 19:17:46.28827	0	Y			Y	Y	Y	N	N	N	Read Only		60	2020-03-27 19:17:46.28827	0	col-md-6 mb-3	0	\N
0	157	\N	0	15	272	2020-03-27 17:26:50.463275	0	\N			Y	Y	Y	Y	Y	N	Table		30	2020-03-27 17:26:50.463275	0	col-md-6 mb-3	0	\N
0	169	\N	0	17	105	2020-03-27 17:44:09.129254	0	@#AD_Client_ID@			Y	Y	Y	Y	Y	N	Client		10	2020-03-27 17:44:09.129254	0	col-md-6 mb-3	0	\N
0	130	\N	0	5	394	2020-03-25 18:10:50.050573	0	\N			Y	Y	Y	N	N	Y	OrderBy Clause		105	2020-03-25 18:10:50.050573	0	col-md-6 mb-3	0	\N
0	170	\N	0	17	106	2020-03-27 17:44:47.010521	0	@#AD_Org_ID@			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 17:44:47.010521	0	col-md-6 mb-3	0	\N
0	173	\N	0	17	103	2020-03-27 17:47:27.765381	0	\N			Y	Y	Y	N	N	N	Name		40	2020-03-27 17:47:27.765381	0	col-md-12 mb-3	0	\N
0	174	\N	0	18	376	2020-03-27 17:51:19.714187	0	env=@#AD_Client_ID@			Y	Y	Y	Y	N	N	Client		10	2020-03-27 17:51:19.714187	0	col-md-6 mb-3	0	\N
0	142	\N	0	13	117	2020-03-27 10:05:37.568418	0	\N			Y	Y	Y	N	N	Y	Active		80	2020-03-27 10:05:37.568418	0	col-md-6 mb-3	0	\N
0	150	\N	0	14	407	2020-03-27 10:30:14.573512	0	\N			Y	Y	Y	Y	N	Y	Extension		40	2020-03-27 10:30:14.573512	0	col-md-6 mb-3	0	\N
0	194	\N	0	20	285	2020-03-27 18:16:09.448212	0	\N			Y	Y	Y	N	N	N	Model Provider Class		50	2020-03-27 18:16:09.448212	0	col-md-12 mb-3	0	\N
0	149	\N	0	14	406	2020-03-27 10:29:48.369191	0	\N			Y	Y	Y	Y	Y	N	Reference		30	2020-03-27 10:29:48.369191	0	col-md-6 mb-3	0	\N
0	134	\N	0	5	208	2020-03-25 21:38:32.989363	0	\N			Y	Y	Y	N	N	Y	Parent Column		55	2020-03-25 21:38:32.989363	0	col-md-6 mb-3	0	AD_Table_ID eq <@AD_Table_ID@>
0	127	\N	0	6	393	2020-03-24 20:28:54.776716	0	\N			Y	Y	Y	N	N	Y	Dynamic Validation		55	2020-03-24 20:28:54.776716	0	col-md-6 mb-3	0
0	195	\N	0	20	284	2020-03-27 18:16:25.094969	0	\N			Y	Y	Y	N	N	N	Service Provider Class		60	2020-03-27 18:16:25.094969	0	col-md-12 mb-3	0	\N
0	205	\N	0	16	142	2020-03-27 18:30:50.802915	0	Y			Y	Y	Y	N	N	N	Active		60	2020-03-27 18:30:50.802915	0	col-md-6 mb-3	0	\N
0	208	\N	0	20	277	2020-03-27 18:33:39.293603	0	Y			Y	Y	Y	N	N	N	Active		70	2020-03-27 18:33:39.293603	0	col-md-6 mb-3	0	\N
0	213	\N	0	22	414	2020-03-27 18:56:51.575099	0	\N			Y	Y	Y	Y	N	N	Role		40	2020-03-27 18:56:51.575099	0	col-md-6 mb-3	0	\N
0	265	\N	0	31	315	2020-03-27 20:21:42.431868	0	\N			Y	Y	Y	N	N	N	Active		60	2020-03-27 20:21:42.431868	0	col-md-6 mb-3	0	\N
0	319	\N	0	34	551	2020-06-15 17:55:40.341392	0	\N	\N	\N	Y	Y	Y	Y	Y	N	User	\N	30	2020-06-15 17:55:40.341392	0	col-md-6 mb-3	0	\N
0	220	\N	0	24	414	2020-03-27 19:12:02.650805	0	\N			Y	Y	Y	Y	Y	Y	Role		30	2020-03-27 19:12:02.650805	0	col-md-6 mb-3	0	\N
0	230	\N	0	25	437	2020-03-27 19:26:07.437714	0	\N			Y	Y	Y	Y	N	N	Resource Name		45	2020-03-27 19:26:07.437714	0	col-md-12 mb-3	0	\N
0	232	\N	0	26	12	2020-03-27 19:29:47.604261	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 19:29:47.604261	0	col-md-6 mb-3	0	\N
0	233	\N	0	26	18	2020-03-27 19:30:09.393594	0	\N			Y	Y	Y	Y	N	N	Name		30	2020-03-27 19:30:09.393594	0	col-md-12 mb-3	0	\N
0	234	\N	0	26	21	2020-03-27 19:30:34.389409	0	\N			Y	Y	Y	Y	N	N	Client Secret		30	2020-03-27 19:30:34.389409	0	col-md-12 mb-3	0	\N
0	238	\N	0	27	440	2020-03-27 19:54:32.985976	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 19:54:32.985976	0	col-md-6 mb-3	0	\N
0	240	\N	0	27	438	2020-03-27 19:55:24.980374	0	\N			Y	Y	Y	Y	Y	N	OAuth Client		30	2020-03-27 19:55:24.980374	0	col-md-6 mb-3	0	\N
0	244	\N	0	28	441	2020-03-27 20:00:13.361546	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 20:00:13.361546	0	col-md-6 mb-3	0	\N
0	246	\N	0	28	438	2020-03-27 20:00:42.877373	0	\N			Y	Y	Y	Y	N	Y	OAuth Client		40	2020-03-27 20:00:42.877373	0	col-md-6 mb-3	0	\N
0	247	\N	0	28	442	2020-03-27 20:03:11.191999	0	\N			Y	Y	Y	N	N	N	Active		60	2020-03-27 20:03:11.191999	0	col-md-6 mb-3	0	\N
0	248	\N	0	29	325	2020-03-27 20:07:04.058533	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 20:07:04.058533	0	col-md-6 mb-3	0	\N
0	251	\N	0	29	333	2020-03-27 20:07:53.039132	0	\N			Y	Y	Y	Y	N	Y	MimeType		40	2020-03-27 20:07:53.039132	0	col-md-6 mb-3	0	\N
0	252	\N	0	29	334	2020-03-27 20:08:16.451155	0	\N			Y	Y	Y	N	N	N	Description		50	2020-03-27 20:08:16.451155	0	col-md-12 mb-3	0	\N
0	254	\N	0	30	356	2020-03-27 20:11:16.826005	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 20:11:16.826005	0	col-md-6 mb-3	0	\N
0	257	\N	0	30	339	2020-03-27 20:12:17.827031	0	\N			Y	Y	Y	N	N	N	Attributes		40	2020-03-27 20:12:17.827031	0	col-md-12 mb-3	0	\N
0	258	\N	0	30	341	2020-03-27 20:12:37.079977	0	\N			Y	Y	Y	N	N	N	Internal Storage		50	2020-03-27 20:12:37.079977	0	col-md-6 mb-3	0	\N
0	259	\N	0	30	342	2020-03-27 20:13:06.256394	0	\N			Y	Y	Y	N	N	Y	Security Access		60	2020-03-27 20:13:06.256394	0	col-md-6 mb-3	0	\N
0	260	\N	0	30	358	2020-03-27 20:13:18.665355	0	\N			Y	Y	Y	N	N	N	Active		70	2020-03-27 20:13:18.665355	0	col-md-6 mb-3	0	\N
0	269	\N	0	26	452	2020-04-07 12:49:44.991718	0	3600	\N	\N	Y	Y	Y	Y	N	N	Token Expires In (milliseconds)	\N	35	2020-04-07 12:49:44.991718	0	col-md-6 mb-3	0	\N
0	256	\N	0	30	337	2020-03-27 20:11:58.829847	0	\N			Y	Y	Y	N	N	N	Folder Name		35	2020-03-27 20:11:58.829847	0	col-md-6 mb-3	0	\N
0	188	\N	0	19	308	2020-03-27 18:02:32.709982	0	\N			Y	Y	Y	Y	N	N	Value		30	2020-03-27 18:02:32.709982	0	col-md-6 mb-3	0	\N
0	272	\N	0	19	311	2020-04-10 13:07:49.814392	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Extension	\N	40	2020-04-10 13:07:49.814392	0	col-md-6 mb-3	0	\N
0	283	\N	0	16	529	2020-04-16 09:25:27.098113	0	\N	\N	\N	Y	Y	Y	N	N	N	Mail Config	\N	50	2020-04-16 09:25:27.098113	0	col-md-6 mb-3	0	\N
0	296	\N	0	11	169	2020-04-16 09:32:37.053157	0	\N	\N	\N	Y	Y	Y	N	N	N	Active	\N	60	2020-04-16 09:32:37.053157	0	col-md-6 mb-3	0	\N
0	297	\N	0	12	173	2020-04-16 09:40:02.540575	0	\N	\N	\N	Y	Y	Y	Y	N	N	Client	\N	10	2020-04-16 09:40:02.540575	0	col-md-6 mb-3	0	\N
0	298	\N	0	12	174	2020-04-16 09:40:16.732255	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-04-16 09:40:16.732255	0	col-md-6 mb-3	0	\N
0	302	\N	0	12	182	2020-04-16 09:45:34.372932	0	\N	\N	\N	Y	Y	Y	N	N	N	Parent Node	\N	60	2020-04-16 09:45:34.372932	0	col-md-6 mb-3	0	\N
0	301	\N	0	12	183	2020-04-16 09:44:56.090085	0	\N	\N	\N	Y	Y	Y	N	N	Y	Sequence	\N	70	2020-04-16 09:44:56.090085	0	col-md-6 mb-3	0	\N
0	305	\N	0	12	180	2020-04-16 10:02:29.529177	0	\N	\N	\N	Y	Y	Y	Y	N	N	Menu	\N	25	2020-04-16 10:02:29.529177	0	col-md-6 mb-3	0	\N
0	279	\N	0	32	511	2020-04-16 09:09:07.26336	0	\N	\N	\N	Y	Y	Y	Y	N	N	Subject	\N	40	2020-04-16 09:09:07.26336	0	col-md-6 mb-3	0	\N
0	306	\N	0	32	509	2020-04-16 10:50:40.715535	0	\N	\N	\N	Y	Y	Y	N	N	Y	Description	\N	35	2020-04-16 10:50:40.715535	0	col-md-6 mb-3	0	\N
0	312	\N	0	21	534	2020-04-19 16:43:37.674101	0	\N	\N	\N	Y	Y	Y	Y	N	Y	User Level	\N	35	2020-04-19 16:43:37.674101	0	col-md-6 mb-3	0	\N
0	274	\N	0	21	456	2020-04-10 14:36:24.920804	0	\N	\N	\N	Y	Y	Y	Y	N	N	Name	\N	40	2020-04-10 14:36:24.920804	0	col-md-6 mb-3	0	\N
0	315	\N	0	26	537	2020-04-23 13:53:50.69138	0	Y	\N	\N	Y	Y	Y	N	N	Y	Refresh Token Expires	\N	60	2020-04-23 13:53:50.69138	0	col-md-6 mb-3	0	\N
0	235	\N	0	26	273	2020-03-27 19:31:13.400534	0	N			Y	Y	Y	N	N	Y	Admin		70	2020-03-27 19:31:13.400534	0	col-md-6 mb-3	0	\N
0	237	\N	0	26	22	2020-03-27 19:31:44.914878	0	\N			Y	Y	Y	N	N	N	Account Locked		80	2020-03-27 19:31:44.914878	0	col-md-6 mb-3	0	\N
0	203	\N	0	21	214	2020-03-27 18:28:28.046758	0	\N			Y	Y	Y	N	N	N	Date Account Locked		80	2020-03-27 18:28:28.046758	0	col-md-6 mb-3	0	\N
0	321	\N	0	34	556	2020-06-15 17:56:32.27734	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	50	2020-06-15 17:56:32.27734	0	col-md-6 mb-3	0	\N
0	322	\N	0	35	563	2020-06-16 19:59:30.3278	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2020-06-16 19:59:30.3278	0	col-md-6 mb-3	0	\N
0	323	\N	0	35	564	2020-06-16 19:59:55.667573	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-06-16 19:59:55.667573	0	col-md-6 mb-3	0	\N
0	82	138a5b4f-d5ce-42ca-a387-6cf1fd15bf3d	0	5	65	2019-12-30 21:10:46.261482	0		Comment or Hint	\N	Y	Y	Y	N	N	N	Comment/Help		60	2019-12-30 21:10:46.261482	0	col-md-6 mb-3	0	\N
0	83	29559fd5-d18a-4b3e-92ec-2d56b797daf5	0	5	67	2019-12-30 21:10:46.265359	0	@SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM AD_Tab WHERE AD_Window_ID=@AD_Window_ID@	Method of ordering records; lowest number comes first	\N	Y	Y	Y	Y	N	Y	Sequence		45	2019-12-30 21:10:46.265359	100	col-md-6 mb-3	0	\N
0	85	6a389c16-092e-4af1-a9cc-21b80ff9b891	0	6	69	2019-12-30 21:10:46.277329	0		Alphanumeric identifier of the entity	\N	Y	Y	Y	Y	N	N	Label		45	2019-12-30 21:10:46.277329	0	col-md-6 mb-3	0	\N
0	86	1cdfe428-1623-4f0b-97ae-dfbe139a157a	0	6	70	2019-12-30 21:10:46.282984	0		Optional short description of the record	\N	Y	Y	Y	N	N	N	Description		50	2019-12-30 21:10:46.282984	100	col-md-6 mb-3	0	\N
0	87	beab22bf-0e25-4b37-9244-9032afe961f1	0	6	71	2019-12-30 21:10:46.288816	0		Comment or Hint	\N	Y	Y	Y	N	N	N	Comment/Help		60	2019-12-30 21:10:46.288816	100	col-md-6 mb-3	0	\N
0	158	\N	0	15	297	2020-03-27 17:27:09.57144	0	\N			Y	Y	Y	Y	N	Y	Extension		40	2020-03-27 17:27:09.57144	0	col-md-6 mb-3	0	\N
0	89	a3d9dd29-57c6-4e40-9f29-f2cd8a03fd21	0	5	85	2019-12-30 21:10:46.297903	0		Database Table information	\N	Y	Y	Y	Y	N	N	Table		70	2019-12-30 21:10:46.297903	100	col-md-6 mb-3	0	\N
0	90	6ce82b0d-d89e-4261-a187-7f86ef6622ff	0	6	74	2019-12-30 21:10:46.302315	0	Y	Determines, if this field is displayed	\N	Y	Y	Y	Y	N	N	Displayed		140	2019-12-30 21:10:46.302315	100	col-md-6 mb-3	0	\N
0	105	47dfa528-dc56-4acb-a3b8-9c6e3b48c894	0	7	137	2019-12-30 21:10:46.416085	0	Y	The record is active in the system	\N	Y	Y	Y	Y	N	N	Active		40	2019-12-30 21:10:46.416085	0	col-md-6 mb-3	0	\N
0	92	acaa41a0-3035-4ded-931f-ed066e9922ec	0	6	76	2019-12-30 21:10:46.317988	0		Displayed on same line as previous field	\N	Y	Y	Y	Y	N	Y	Same Line		480	2019-12-30 21:10:46.317988	100	col-md-6 mb-3	0	\N
0	93	1fce8558-2734-42b8-8111-d5b60d39b347	0	2	42	2019-12-30 21:10:46.322454	0		Alphanumeric identifier of the entity	\N	Y	Y	Y	Y	N	N	Name		40	2019-12-30 21:10:46.322454	0	col-md-6 mb-3	0	\N
0	94	ac5fd13d-cf72-405d-90a8-1344546524f1	0	2	43	2019-12-30 21:10:46.326998	0		Optional short description of the record	\N	Y	Y	Y	N	N	N	Description		50	2019-12-30 21:10:46.326998	0	col-md-6 mb-3	0	\N
0	95	f9e865f9-02d4-4894-9963-6ac1ba49871e	0	2	44	2019-12-30 21:10:46.331301	0		Comment or Hint	\N	Y	Y	Y	N	N	N	Comment/Help		60	2019-12-30 21:10:46.331301	0	col-md-6 mb-3	0	\N
0	96	a967024f-fe99-4383-8156-6523be39c6c4	0	2	45	2019-12-30 21:10:46.335995	0		Name of the table in the database	\N	Y	Y	Y	Y	N	N	DB Table Name		30	2019-12-30 21:10:46.335995	0	col-md-6 mb-3	0	\N
0	97	f9327f9a-516e-46bd-bf15-de2b75976b4c	0	2	107	2019-12-30 21:10:46.371624	0	Y	The record is active in the system	\N	Y	Y	Y	Y	N	N	Active		70	2019-12-30 21:10:46.371624	0	col-md-6 mb-3	0	\N
0	98	75740b9d-4968-4cc1-b781-611cc958e93e	0	2	86	2019-12-30 21:10:46.381522	0	@#AD_Client_ID@	Client/Tenant for this installation.	\N	Y	Y	N	Y	N	N	Client		10	2019-12-30 21:10:46.381522	0	col-md-6 mb-3	0	\N
0	99	39f0d78a-47dc-4a9f-ab36-4934eef731e3	0	4	122	2019-12-30 21:10:46.391984	0	Y	The record is active in the system	\N	Y	Y	Y	Y	N	N	Active		60	2019-12-30 21:10:46.391984	0	col-md-6 mb-3	0	\N
0	100	0cc509e4-76a7-490f-ac39-537174604113	0	4	92	2019-12-30 21:10:46.396805	0	@#AD_Client_ID@	Client/Tenant for this installation.	\N	Y	Y	N	Y	N	N	Client		10	2019-12-30 21:10:46.396805	0	col-md-6 mb-3	0	\N
0	101	3652b6a0-2ce8-43d4-832e-05ed69745c31	0	5	127	2019-12-30 21:10:46.401358	0	Y	The record is active in the system	\N	Y	Y	Y	Y	N	N	Active		260	2019-12-30 21:10:46.401358	100	col-md-6 mb-3	0	\N
0	102	e6bd9635-5d70-4436-8a9a-540935d56fd9	0	5	94	2019-12-30 21:10:46.405509	0	@AD_Client_ID@	Client/Tenant for this installation.	\N	Y	Y	N	Y	N	N	Client		10	2019-12-30 21:10:46.405509	0	col-md-6 mb-3	0	\N
0	103	4fb20a68-a8f3-454e-b66a-4b2a75805c60	0	6	132	2019-12-30 21:10:46.409061	0	Y	The record is active in the system	\N	Y	Y	Y	Y	N	N	Active		70	2019-12-30 21:10:46.409061	0	col-md-6 mb-3	0	\N
0	104	c6e58490-d153-445d-9af2-cd821813df3c	0	6	96	2019-12-30 21:10:46.412553	0	@AD_Client_ID@	Client/Tenant for this installation.	\N	Y	Y	N	Y	N	N	Client		10	2019-12-30 21:10:46.412553	0	col-md-6 mb-3	0	\N
0	107	5618ae0a-7458-4321-99ba-1be371eb23df	0	6	159	2019-12-30 21:10:46.42283	0		Field is read only	\N	Y	Y	Y	Y	N	Y	Read Only		180	2019-12-30 21:10:46.42283	100	col-md-6 mb-3	0	\N
0	108	d57aba26-13b8-49da-8c9d-139bf79d42dd	0	5	160	2019-12-30 21:10:46.425705	0		Field is read only	\N	Y	Y	Y	Y	N	N	Read Only		240	2019-12-30 21:10:46.425705	100	col-md-6 mb-3	0	\N
0	109	558bceda-ca93-4640-b9ca-032b7edad273	0	4	93	2020-01-11 15:43:55.554263	0	@#AD_Org_ID@	Organizational entity within client	\N	Y	Y	N	Y	N	Y	Organization		20	2020-01-11 15:43:55.554263	0	col-md-6 mb-3	0	\N
0	110	8d371099-54d8-4d10-8dc6-d568cf1194a7	0	5	95	2019-12-30 21:10:46.434906	0	@AD_Org_ID@	Organizational entity within client	\N	Y	Y	N	Y	N	Y	Organization		20	2019-12-30 21:10:46.434906	0	col-md-6 mb-3	0	\N
0	111	ec0a330d-59c7-4d2e-a670-b6777d60c61d	0	6	97	2019-12-30 21:10:46.442318	0	@AD_Org_ID@	Organizational entity within client	\N	Y	Y	Y	Y	N	Y	Organization		20	2019-12-30 21:10:46.442318	0	col-md-6 mb-3	0	\N
0	112	324575e4-b281-4920-8aef-d9babca938d9	0	2	87	2019-12-30 21:10:46.447958	0	@#AD_Org_ID@	Organizational entity within client	\N	Y	Y	N	Y	N	Y	Organization		20	2019-12-30 21:10:46.447958	0	col-md-6 mb-3	0	\N
0	113	99ed4a9f-b750-4497-8f79-dbae28f9f152	0	2	186	2019-12-30 21:10:46.452829	0	N	This is a view	\N	Y	Y	Y	Y	N	Y	View		80	2019-12-30 21:10:46.452829	0	col-md-6 mb-3	0	\N
0	114	5564b213-fbed-41c2-ab69-cb212d797046	0	5	192	2019-12-30 21:10:46.458012	0		Hierarchical Tab Level (0 = top)	\N	Y	Y	Y	Y	N	Y	Tab Level		100	2019-12-30 21:10:46.458012	100	col-md-6 mb-3	0	\N
0	166	\N	0	16	185	2020-03-27 17:34:48.086519	0	\N			Y	Y	Y	Y	N	N	Identifier		30	2020-03-27 17:34:48.086519	0	col-md-6 mb-3	0	\N
0	115	2d938575-0f12-459d-9de5-67d9e29091e6	0	7	194	2019-12-30 21:10:46.463102	0		Search key for the record in the format required - must be unique	\N	Y	Y	Y	Y	N	N	Search Key		30	2019-12-30 21:10:46.463102	0	col-md-12 mb-3	0	\N
0	180	\N	0	18	378	2020-03-27 17:53:36.384609	0	Y			Y	Y	Y	N	N	N	Active		70	2020-03-27 17:53:36.384609	0	col-md-6 mb-3	0	\N
0	184	\N	0	18	391	2020-03-27 17:55:09.104493	0	\N			Y	Y	Y	N	N	Y	Time Pattern		110	2020-03-27 17:55:09.104493	0	col-md-6 mb-3	0	\N
0	185	\N	0	18	389	2020-03-27 17:55:33.243785	0	Y			Y	Y	Y	N	N	N	Decimal Point		110	2020-03-27 17:55:33.243785	0	col-md-6 mb-3	0	\N
0	177	\N	0	18	383	2020-03-27 17:52:29.553458	0	\N			Y	Y	Y	N	N	N	Name		40	2020-03-27 17:52:29.553458	0	col-md-12 mb-3	0	\N
0	196	\N	0	20	290	2020-03-27 18:16:53.742742	0	\N			Y	Y	Y	N	N	Y	Sequence		45	2020-03-27 18:16:53.742742	0	col-md-6 mb-3	0	\N
0	223	\N	0	25	225	2020-03-27 19:14:38.434499	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 19:14:38.434499	0	col-md-6 mb-3	0	\N
0	204	\N	0	21	147	2020-03-27 18:28:51.722167	0	Y			Y	Y	Y	N	N	N	Active		90	2020-03-27 18:28:51.722167	0	col-md-6 mb-3	0	\N
0	119	6565885f-4503-443d-9f92-b2c919961a70	0	5	200	2019-12-30 21:10:46.479173	100	Y	The user can insert a new Record	\N	Y	Y	Y	Y	N	Y	Insert Record		250	2019-12-30 21:10:46.479173	100	col-md-6 mb-3	0	\N
0	120	800114f5-801e-4f1a-8fd4-4fc24d4a85f7	0	6	203	2019-12-30 21:10:46.483508	100		Data entry is required in this column	\N	Y	Y	Y	N	N	N	Mandatory		410	2019-12-30 21:10:46.483508	100	col-md-6 mb-3	0	\N
0	121	26931797-3bde-42ae-acfc-e61ab4948abd	0	6	212	2019-12-30 21:10:46.501202	100	Y		\N	Y	Y	Y	N	N	Y	Show in Grid		150	2019-12-30 21:10:46.501202	100	col-md-6 mb-3	0	\N
0	122	94aae929-609b-4c67-9c02-4ee911ce4d17	0	6	207	2019-12-30 21:10:46.505232	100			\N	Y	Y	Y	N	N	N	Placeholder		70	2019-12-30 21:10:46.505232	100	col-md-6 mb-3	0	\N
0	88	6c9d35e4-7768-4c58-a14a-e41579157851	0	6	73	2019-12-30 21:10:46.293564	0		Column in the table	\N	Y	Y	Y	N	N	N	Column		90	2019-12-30 21:10:46.293564	0	col-md-6 mb-3	0	AD_Table_ID eq <@$AD_Window_ID@|@$AD_Parent_Tab_ID@|AD_Table_ID>
0	266	\N	0	31	322	2020-03-27 20:22:09.193138	0	\N			Y	Y	Y	Y	Y	N	Media Folder		30	2020-03-27 20:22:09.193138	0	col-md-6 mb-3	0	\N
0	91	b0bad5d9-74f4-4431-8196-bbe7ebfb7522	0	6	75	2019-12-30 21:10:46.313406	0	@SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM AD_Field WHERE AD_Tab_ID=@AD_Tab_ID@	Method of ordering records; lowest number comes first	\N	Y	Y	Y	Y	N	Y	Sequence		40	2019-12-30 21:10:46.313406	0	col-md-6 mb-3	0	\N
0	124	\N	0	2	289	2020-02-26 15:44:37.357879	0	N			Y	Y	Y	N	N	N	Translated		90	2020-02-26 15:44:37.357879	0	col-md-6 mb-3	0	\N
0	131	\N	0	4	294	2020-03-25 19:59:07.352232	0	\N			Y	Y	Y	Y	N	Y	Extension		35	2020-03-25 19:59:07.352232	0	col-md-6 mb-3	0	\N
0	136	\N	0	13	91	2020-03-27 09:58:05.275706	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 09:58:05.275706	0	col-md-6 mb-3	0	\N
0	135	\N	0	13	90	2020-03-27 09:57:41.824881	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 09:57:41.824881	0	col-md-6 mb-3	0	\N
0	143	\N	0	14	396	2020-03-27 10:14:47.520682	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 10:14:47.520682	0	col-md-6 mb-3	0	\N
0	144	\N	0	14	397	2020-03-27 10:15:06.193177	0	\N			Y	Y	Y	Y	N	Y	Organizatoin		20	2020-03-27 10:15:06.193177	0	col-md-6 mb-3	0	\N
0	145	\N	0	14	403	2020-03-27 10:15:40.504836	0	\N			Y	Y	Y	N	N	N	Search Key		50	2020-03-27 10:15:40.504836	0	col-md-6 mb-3	0	\N
0	267	\N	0	2	450	2020-04-06 19:09:26.910582	0	N	\N	\N	Y	Y	Y	N	N	Y	Public	\N	100	2020-04-06 19:09:26.910582	0	col-md-6 mb-3	0	\N
0	190	\N	0	20	275	2020-03-27 18:13:34.080513	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 18:13:34.080513	0	col-md-6 mb-3	0	\N
0	198	\N	0	21	101	2020-03-27 18:25:57.418974	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 18:25:57.418974	0	col-md-6 mb-3	0	\N
0	206	\N	0	17	152	2020-03-27 18:31:14.936339	0	Y			Y	Y	Y	N	N	N	Active		70	2020-03-27 18:31:14.936339	0	col-md-6 mb-3	0	\N
0	270	\N	0	25	453	2020-04-10 11:13:15.768725	0	Y	\N	\N	Y	Y	Y	N	N	N	Exactly Match	\N	50	2020-04-10 11:13:15.768725	0	col-md-6 mb-3	0	\N
0	151	\N	0	3	409	2020-03-27 10:52:01.90138	0	\N			Y	Y	Y	N	N	Y	Reference Value		90	2020-03-27 10:52:01.90138	0	col-md-6 mb-3	0	ValidationType eq 'T' or ValidationType eq 'L'
0	189	\N	0	19	309	2020-03-27 18:03:01.575965	0	\N			Y	Y	Y	N	N	N	Attributes		50	2020-03-27 18:03:01.575965	0	col-md-12 mb-3	0	\N
0	123	\N	0	3	288	2020-02-26 15:44:09.199595	0	N			Y	Y	Y	N	N	Y	Translatable		130	2020-02-26 15:44:09.199595	0	col-md-6 mb-3	0	\N
0	214	\N	0	23	425	2020-03-27 19:08:18.489529	0	@#AD_Client_ID@			Y	Y	Y	Y	N	N	Client		10	2020-03-27 19:08:18.489529	0	col-md-6 mb-3	0	\N
0	217	\N	0	23	427	2020-03-27 19:09:10.912297	0	Y			Y	Y	Y	N	N	N	Active		40	2020-03-27 19:09:10.912297	0	col-md-6 mb-3	0	\N
0	219	\N	0	24	416	2020-03-27 19:11:51.178333	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 19:11:51.178333	0	col-md-6 mb-3	0	\N
0	224	\N	0	25	227	2020-03-27 19:14:52.835614	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 19:14:52.835614	0	col-md-6 mb-3	0	\N
0	207	\N	0	19	303	2020-03-27 18:33:00.907841	0	Y			Y	Y	Y	N	N	N	Active		60	2020-03-27 18:33:00.907841	0	col-md-6 mb-3	0	\N
0	225	\N	0	25	223	2020-03-27 19:15:07.821217	0	\N			Y	Y	Y	Y	N	Y	Resource		40	2020-03-27 19:15:07.821217	0	col-md-6 mb-3	0	\N
0	231	\N	0	26	11	2020-03-27 19:29:30.43132	0	\N			Y	Y	Y	Y	N	N	Client		10	2020-03-27 19:29:30.43132	0	col-md-6 mb-3	0	\N
0	249	\N	0	29	326	2020-03-27 20:07:16.274246	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 20:07:16.274246	0	col-md-6 mb-3	0	\N
0	255	\N	0	30	357	2020-03-27 20:11:33.380587	0	\N			Y	Y	Y	Y	N	Y	Organization		20	2020-03-27 20:11:33.380587	0	col-md-6 mb-3	0	\N
0	275	\N	0	19	457	2020-04-15 22:43:22.475775	0	\N	\N	\N	Y	Y	Y	Y	N	N	Service Type	\N	55	2020-04-15 22:43:22.475775	0	col-md-6 mb-3	0	\N
0	261	\N	0	30	449	2020-03-27 20:16:20.01783	0	\N			Y	Y	Y	Y	N	N	Provider		30	2020-03-27 20:16:20.01783	0	col-md-6 mb-3	0	\N
0	307	\N	0	33	527	2020-04-16 10:58:26.97799	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Mail Port	\N	35	2020-04-16 10:58:26.97799	0	col-md-6 mb-3	0	\N
0	291	\N	0	11	163	2020-04-16 09:30:59.015642	0	\N	\N	\N	Y	Y	Y	Y	N	N	Client	\N	10	2020-04-16 09:30:59.015642	0	col-md-6 mb-3	0	\N
0	285	\N	0	33	516	2020-04-16 09:28:00.524388	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-04-16 09:28:00.524388	0	col-md-6 mb-3	0	\N
0	286	\N	0	33	526	2020-04-16 09:28:34.288405	0	\N	\N	\N	Y	Y	Y	Y	N	N	Mail Host	\N	30	2020-04-16 09:28:34.288405	0	col-md-6 mb-3	0	\N
0	287	\N	0	33	522	2020-04-16 09:28:58.732051	0	\N	\N	\N	Y	Y	Y	Y	N	N	Request Email	\N	40	2020-04-16 09:28:58.732051	0	col-md-6 mb-3	0	\N
0	288	\N	0	33	523	2020-04-16 09:29:18.315288	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Request Folder	\N	50	2020-04-16 09:29:18.315288	0	col-md-6 mb-3	0	\N
0	289	\N	0	33	524	2020-04-16 09:29:39.171802	0	\N	\N	\N	Y	Y	Y	Y	N	N	Request User	\N	60	2020-04-16 09:29:39.171802	0	col-md-6 mb-3	0	\N
0	292	\N	0	11	164	2020-04-16 09:31:23.076812	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-04-16 09:31:23.076812	0	col-md-6 mb-3	0	\N
0	293	\N	0	11	170	2020-04-16 09:31:57.263447	0	\N	\N	\N	Y	Y	Y	N	N	N	Name	\N	30	2020-04-16 09:31:57.263447	0	col-md-12 mb-3	0	\N
0	294	\N	0	11	171	2020-04-16 09:32:11.657521	0	\N	\N	\N	Y	Y	Y	N	N	N	Description	\N	40	2020-04-16 09:32:11.657521	0	col-md-12 mb-3	0	\N
0	299	\N	0	12	172	2020-04-16 09:40:48.375293	0	\N	\N	\N	Y	Y	Y	Y	N	N	Name	\N	30	2020-04-16 09:40:48.375293	0	col-md-12 mb-3	0	\N
0	300	\N	0	12	175	2020-04-16 09:41:47.748719	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	40	2020-04-16 09:41:47.748719	0	col-md-6 mb-3	0	\N
0	303	\N	0	12	368	2020-04-16 09:46:10.669741	0	N	\N	\N	Y	Y	Y	N	N	Y	Summary	\N	50	2020-04-16 09:46:10.669741	0	col-md-6 mb-3	0	\N
0	280	\N	0	32	512	2020-04-16 09:09:27.190543	0	\N	\N	\N	Y	Y	N	N	N	N	Template	\N	50	2020-04-16 09:09:27.190543	0	col-md-12 mb-3	0	\N
0	310	\N	0	16	532	2020-04-19 13:10:19.814497	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Menu	\N	55	2020-04-19 13:10:19.814497	0	col-md-6 mb-3	0	\N
0	313	\N	0	26	535	2020-04-19 20:25:13.800097	0	\N	\N	\N	Y	Y	Y	Y	N	Y	User	\N	40	2020-04-19 20:25:13.800097	0	col-md-6 mb-3	0	\N
0	236	\N	0	26	13	2020-03-27 19:31:28.243935	0	\N			Y	Y	Y	N	N	Y	Active		90	2020-03-27 19:31:28.243935	0	col-md-6 mb-3	0	\N
0	316	\N	0	21	539	2020-04-26 21:19:16.308382	0	N	\N	\N	Y	Y	Y	N	N	N	Admin	\N	70	2020-04-26 21:19:16.308382	0	col-md-6 mb-3	0	\N
0	317	\N	0	34	554	2020-06-15 17:54:56.337163	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	Y	Client	\N	10	2020-06-15 17:54:56.337163	0	col-md-6 mb-3	0	\N
0	320	\N	0	34	552	2020-06-15 17:56:19.382626	0	\N	\N	\N	Y	Y	Y	Y	N	Y	App	\N	40	2020-06-15 17:56:19.382626	0	col-md-6 mb-3	0	\N
0	324	\N	0	35	570	2020-06-16 20:00:13.146194	0	\N	\N	\N	Y	Y	Y	Y	Y	N	Role	\N	30	2020-06-16 20:00:13.146194	0	col-md-6 mb-3	0	\N
0	325	\N	0	35	571	2020-06-16 20:00:29.039644	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Window	\N	40	2020-06-16 20:00:29.039644	0	col-md-6 mb-3	0	\N
0	326	\N	0	35	565	2020-06-16 20:00:39.836658	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	50	2020-06-16 20:00:39.836658	0	col-md-6 mb-3	0	\N
0	327	\N	0	3	574	2020-08-08 16:13:44.206948	0	Y	\N	\N	Y	Y	Y	N	N	Y	Updatable	\N	100	2020-08-08 16:13:44.206948	0	col-md-6 mb-3	0	\N
0	328	\N	0	15	265	2020-08-08 21:19:28.958384	0	\N	\N	\N	Y	Y	Y	N	N	N	Name	\N	50	2020-08-08 21:19:28.958384	0	col-md-6 mb-3	0	\N
0	160	\N	0	15	266	2020-03-27 17:28:23.93802	0	\N			Y	Y	Y	N	N	Y	Description	Description	60	2020-08-08 21:19:37.184	0	col-md-6 mb-3	0	\N
0	329	\N	0	36	576	2020-09-14 20:13:31.879132	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2020-09-14 20:13:31.879132	0	col-md-6 mb-3	0	\N
0	330	\N	0	36	577	2020-09-14 20:13:52.184959	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-09-14 20:13:52.184959	0	col-md-6 mb-3	0	\N
0	364	\N	0	40	637	2020-12-15 17:56:44.81829	0	\N	\N	\N	Y	Y	Y	N	Y	Y	Status	\N	60	2020-12-15 18:57:11.228	0	col-md-6 mb-3	0	\N
0	331	\N	0	36	584	2020-09-14 20:15:31.37047	0	\N	\N	\N	Y	Y	Y	Y	N	N	Name	\N	30	2020-09-14 20:16:00.729	0	col-md-12 mb-3	0	\N
0	332	\N	0	36	583	2020-09-14 20:15:49.650412	0	\N	\N	\N	Y	Y	Y	Y	N	N	Value	\N	40	2020-09-14 20:16:04.812	0	col-md-12 mb-3	0	\N
0	333	\N	0	36	585	2020-09-14 20:16:16.773257	0	\N	\N	\N	Y	Y	Y	N	N	N	Description	\N	50	2020-09-14 20:16:16.773257	0	col-md-6 mb-3	0	\N
0	334	\N	0	36	578	2020-09-14 20:16:29.588874	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	60	2020-09-14 20:16:29.588874	0	col-md-6 mb-3	0	\N
0	335	\N	0	37	588	2020-10-29 10:06:15.862342	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2020-10-29 10:06:15.862342	0	col-md-6 mb-3	0	\N
0	366	\N	0	40	639	2020-12-15 17:57:30.707479	0	\N	\N	\N	Y	Y	Y	N	Y	Y	Last End Time	\N	80	2020-12-15 17:57:30.707479	0	col-md-6 mb-3	0	\N
0	367	\N	0	40	632	2020-12-15 17:58:01.765509	0	Y	\N	\N	Y	Y	Y	Y	N	N	Active	\N	90	2020-12-15 17:58:01.765509	0	col-md-6 mb-3	0	\N
0	368	\N	0	41	646	2020-12-28 13:12:23.121977	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2020-12-28 13:12:23.121977	0	col-md-6 mb-3	0	\N
0	358	\N	0	39	618	2020-12-15 17:52:14.60173	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	90	2021-04-28 10:49:07.101	0	col-md-6 mb-3	0	\N
0	369	\N	0	41	647	2020-12-28 13:12:40.886098	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-12-28 13:12:40.886098	0	col-md-6 mb-3	0	\N
0	336	\N	0	37	589	2020-10-29 10:06:47.554307	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-10-29 10:10:53.393	0	col-md-6 mb-3	0	\N
0	340	\N	0	38	600	2020-10-29 10:12:01.325686	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2020-10-29 10:12:01.325686	0	col-md-6 mb-3	0	\N
0	341	\N	0	38	601	2020-10-29 10:12:23.027879	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-10-29 10:12:23.027879	0	col-md-6 mb-3	0	\N
0	342	\N	0	38	613	2020-10-29 10:12:34.765728	0	\N	\N	\N	Y	Y	Y	Y	N	N	Value	\N	30	2020-10-29 10:12:34.765728	0	col-md-6 mb-3	0	\N
0	343	\N	0	38	612	2020-10-29 10:12:52.529412	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Type	\N	40	2020-10-29 10:12:52.529412	0	col-md-6 mb-3	0	\N
0	344	\N	0	38	607	2020-10-29 10:13:16.634324	0	\N	\N	\N	Y	Y	Y	Y	N	N	Reference	\N	50	2020-10-29 10:13:16.634324	0	col-md-6 mb-3	0	\N
0	345	\N	0	38	608	2020-10-29 10:13:30.613241	0	\N	\N	\N	Y	Y	Y	N	N	N	Classname	\N	60	2020-10-29 10:13:30.613241	0	col-md-6 mb-3	0	\N
0	346	\N	0	38	609	2020-10-29 10:13:52.584143	0	\N	\N	\N	Y	Y	Y	N	N	N	SQL	\N	70	2020-10-29 10:13:52.584143	0	col-md-6 mb-3	0	\N
0	347	\N	0	38	610	2020-10-29 10:14:14.040635	0	\N	\N	\N	Y	Y	Y	N	N	Y	Constant Value	\N	55	2020-10-29 10:14:14.040635	0	col-md-6 mb-3	0	\N
0	348	\N	0	38	602	2020-10-29 10:14:38.047182	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	80	2020-10-29 10:14:38.047182	0	col-md-6 mb-3	0	\N
0	349	\N	0	38	611	2020-10-29 10:15:08.990095	0	\N	\N	\N	Y	Y	Y	N	N	N	Description	\N	45	2020-10-29 10:15:08.990095	0	col-md-12	0	\N
0	338	\N	0	37	597	2020-10-29 10:08:06.064831	0	\N	\N	\N	Y	Y	Y	Y	N	N	Expression	\N	50	2020-10-29 10:18:35.428	0	col-md-12	0	\N
0	339	\N	0	37	590	2020-10-29 10:08:22.475522	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	60	2020-10-29 10:18:40.642	0	col-md-6 mb-3	0	\N
0	350	\N	0	37	596	2020-10-29 10:18:51.867856	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Table	\N	40	2020-10-29 10:19:30.151	0	col-md-6 mb-3	0	\N
0	351	\N	0	3	219	2020-10-29 13:52:51.317808	0	N	\N	\N	Y	Y	Y	N	N	N	Mandatory	\N	93	2020-10-29 13:52:51.317808	0	col-md-6 mb-3	0	\N
0	353	\N	0	39	617	2020-12-15 17:50:30.026057	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-12-15 17:50:30.026057	0	col-md-6 mb-3	0	\N
0	106	2db8717f-c979-4592-9674-e61b055fa795	0	7	98	2019-12-30 21:10:46.419777	0	@#AD_Client_ID@	Client/Tenant for this installation.	\N	Y	Y	N	Y	Y	N	Client		10	2020-12-28 14:24:19.362	0	col-md-6 mb-3	0	\N
0	352	\N	0	39	616	2020-12-15 17:50:03.743651	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2020-12-15 18:50:47.077	0	col-md-6 mb-3	0	\N
0	354	\N	0	39	628	2020-12-15 17:51:13.497036	0	\N	\N	\N	Y	Y	Y	Y	N	N	Value	\N	30	2020-12-15 17:51:13.497036	0	col-md-12 mb-3	0	\N
0	355	\N	0	39	623	2020-12-15 17:51:27.299616	0	\N	\N	\N	Y	Y	Y	N	N	N	Name	\N	40	2020-12-15 17:51:27.299616	0	col-md-6 mb-3	0	\N
0	357	\N	0	39	625	2020-12-15 17:52:02.654463	0	\N	\N	\N	Y	Y	Y	N	N	N	Help	\N	60	2020-12-15 17:52:02.654463	0	col-md-12 mb-3	0	\N
0	359	\N	0	40	630	2020-12-15 17:54:43.22077	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2020-12-15 17:54:43.22077	0	col-md-6 mb-3	0	\N
0	360	\N	0	40	631	2020-12-15 17:54:57.677845	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2020-12-15 17:54:57.677845	0	col-md-6 mb-3	0	\N
0	361	\N	0	40	642	2020-12-15 17:55:33.00149	0	\N	\N	\N	Y	Y	Y	Y	Y	Y	Job Definition	\N	30	2020-12-15 17:55:33.00149	0	col-md-6 mb-3	0	\N
0	362	\N	0	40	644	2020-12-15 17:56:04.329474	0	\N	\N	\N	Y	Y	Y	Y	N	Y	User	\N	40	2020-12-15 17:56:04.329474	0	col-md-6 mb-3	0	\N
0	363	\N	0	40	641	2020-12-15 17:56:26.163939	0	\N	\N	\N	Y	Y	Y	Y	N	N	Cron Expression	\N	50	2020-12-15 17:56:26.163939	0	col-md-6 mb-3	0	\N
0	365	\N	0	40	640	2020-12-15 17:57:04.573986	0	\N	\N	\N	Y	Y	Y	N	Y	N	Last Start Time	\N	70	2020-12-15 17:57:04.573986	0	col-md-6 mb-3	0	\N
0	370	\N	0	41	645	2020-12-28 13:12:59.227835	0	\N	\N	\N	Y	Y	Y	Y	Y	N	Message	\N	30	2020-12-28 13:12:59.227835	0	col-md-6 mb-3	0	\N
0	371	\N	0	41	648	2020-12-28 13:13:16.686889	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Language	\N	40	2020-12-28 13:13:16.686889	0	col-md-6 mb-3	0	\N
0	372	\N	0	41	654	2020-12-28 13:13:34.252137	0	\N	\N	\N	Y	Y	Y	N	N	N	MsgText	\N	50	2020-12-28 13:13:34.252137	0	col-md-12 mb-3	0	\N
0	374	\N	0	41	649	2020-12-28 13:14:14.662312	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	70	2020-12-28 13:14:14.662312	0	col-md-6 mb-3	0	\N
0	373	\N	0	41	655	2020-12-28 13:13:47.495237	0	\N	\N	\N	Y	Y	Y	N	N	N	MsgTip	\N	60	2020-12-28 14:14:44.628	0	col-md-12 mb-3	0	\N
0	375	\N	0	42	657	2021-01-01 13:05:02.370362	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2021-01-01 13:05:02.370362	0	col-md-6 mb-3	0	\N
0	376	\N	0	42	658	2021-01-01 13:05:22.409687	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2021-01-01 13:05:22.409687	0	col-md-6 mb-3	0	\N
0	377	\N	0	42	656	2021-01-01 13:05:56.36197	0	\N	\N	\N	Y	Y	Y	Y	Y	N	Message Template	\N	30	2021-01-01 13:05:56.36197	0	col-md-6 mb-3	0	\N
0	379	\N	0	42	665	2021-01-01 13:06:30.336042	0	\N	\N	\N	Y	Y	Y	N	N	N	Description	\N	50	2021-01-01 13:06:30.336042	0	col-md-12 mb-3	0	\N
0	356	\N	0	39	624	2020-12-15 17:51:48.355876	0	\N	\N	\N	Y	Y	Y	N	N	Y	Description	\N	50	2021-01-20 00:25:22.36	0	col-md-6 mb-3	0	\N
0	380	\N	0	42	667	2021-01-01 13:06:47.215751	0	\N	\N	\N	Y	Y	Y	N	N	N	Template	\N	60	2021-01-01 14:08:34.505	0	col-md-12 mb-3	0	\N
0	337	\N	0	37	595	2020-10-29 10:07:12.217598	0	\N	\N	\N	Y	Y	Y	Y	Y	N	App	\N	30	2021-02-06 16:32:50.407	0	col-md-6 mb-3	0	\N
0	381	\N	0	42	666	2021-01-01 13:08:06.301096	0	\N	\N	\N	Y	Y	Y	N	N	N	Subject	\N	55	2021-01-01 14:08:30.428	0	col-md-12 mb-3	0	\N
0	382	\N	0	42	668	2021-01-01 13:20:59.655512	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Language	\N	40	2021-01-01 13:20:59.655512	0	col-md-6 mb-3	0	\N
0	383	\N	0	21	669	2021-01-17 10:38:19.267969	0	Y	\N	\N	Y	Y	Y	Y	N	Y	View Only Active Records	\N	75	2021-01-17 11:38:59.658	0	col-md-6 mb-3	0	\N
0	384	\N	0	3	670	2021-01-19 23:18:50.621322	0	N	\N	\N	Y	Y	Y	N	N	Y	Identifier	\N	94	2021-01-19 23:18:50.621322	0	col-md-6 mb-3	0	\N
0	385	\N	0	26	682	2021-02-06 15:33:51.620955	0	\N	\N	\N	Y	Y	Y	Y	Y	N	App	\N	25	2021-02-06 15:33:51.620955	0	col-md-6 mb-3	0	\N
0	386	\N	0	43	672	2021-02-06 15:35:10.480871	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	N	N	Client	\N	10	2021-02-06 15:35:10.480871	0	col-md-6 mb-3	0	\N
0	387	\N	0	43	673	2021-02-06 15:35:31.042874	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2021-02-06 15:35:31.042874	0	col-md-6 mb-3	0	\N
0	389	\N	0	43	674	2021-02-06 15:36:02.501061	0	Y	\N	\N	Y	Y	Y	N	N	N	Active	\N	50	2021-02-06 15:36:02.501061	0	col-md-6 mb-3	0	\N
0	388	\N	0	43	679	2021-02-06 15:35:44.571198	0	\N	\N	\N	Y	Y	Y	Y	N	N	Value	\N	30	2021-02-06 16:36:11.046	0	col-md-6 mb-3	0	\N
0	390	\N	0	39	697	2021-04-28 10:48:55.098377	0	\N	\N	\N	Y	Y	Y	\N	N	N	Scripting	\N	70	2021-04-28 10:48:55.098377	0	col-md-6 mb-3	0	\N
0	391	\N	0	44	684	2021-04-28 11:47:32.74467	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2021-04-28 11:47:32.74467	0	col-md-6 mb-3	0	\N
0	392	\N	0	44	685	2021-04-28 11:48:08.639416	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2021-04-28 11:48:08.639416	0	col-md-6 mb-3	0	\N
0	393	\N	0	44	696	2021-04-28 11:48:52.86878	0	\N	\N	\N	Y	Y	Y	Y	N	N	Value	\N	30	2021-04-28 11:48:52.86878	0	col-md-6 mb-3	0	\N
0	394	\N	0	44	691	2021-04-28 11:49:11.921379	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Name	\N	40	2021-04-28 11:49:11.921379	0	col-md-6 mb-3	0	\N
0	395	\N	0	44	692	2021-04-28 11:49:58.629834	0	\N	\N	\N	Y	Y	Y	Y	N	N	Engine Type	\N	50	2021-04-28 11:49:58.629834	0	col-md-6 mb-3	0	\N
0	396	\N	0	44	693	2021-04-28 11:50:17.332714	0	\N	\N	\N	Y	Y	Y	Y	N	N	Script	\N	60	2021-04-28 11:50:17.332714	0	col-md-6 mb-3	0	\N
0	397	\N	0	44	690	2021-04-28 11:50:39.226495	0	Y	\N	\N	Y	Y	Y	Y	N	N	Active	\N	70	2021-04-28 11:50:39.226495	0	col-md-6 mb-3	0	\N
0	400	\N	0	45	727	2021-05-06 20:49:30.752632	0	\N	\N	\N	Y	Y	Y	Y	Y	N	Tab	\N	30	2021-05-06 20:49:30.752632	0	col-md-6 mb-3	0	\N
0	401	\N	0	45	721	2021-05-06 20:49:49.401951	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Name	\N	40	2021-05-06 20:49:49.401951	0	col-md-6 mb-3	0	\N
0	402	\N	0	45	723	2021-05-06 20:50:10.714697	0	\N	\N	\N	Y	Y	Y	Y	N	N	Description	\N	50	2021-05-06 20:50:10.714697	0	col-md-12 mb-3	0	\N
0	420	\N	0	47	749	2021-05-08 20:18:02.797641	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Extension	\N	40	2021-05-08 20:18:43.835	0	col-md-6 mb-3	0	\N
0	403	\N	0	45	726	2021-05-06 20:50:30.31122	0	\N	\N	\N	Y	Y	Y	Y	N	N	Action Name	\N	60	2021-05-06 20:51:38.426	0	col-md-6 mb-3	0	\N
0	404	\N	0	45	722	2021-05-06 20:50:46.18438	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Icon	\N	70	2021-05-06 20:51:45.184	0	col-md-6 mb-3	0	\N
0	405	\N	0	45	725	2021-05-06 20:51:16.402931	0	\N	\N	\N	Y	Y	Y	Y	N	N	Process	\N	80	2021-05-06 20:52:23.63	0	col-md-6 mb-3	0	\N
0	421	\N	0	47	737	2021-05-08 20:18:25.519346	0	\N	\N	\N	Y	Y	Y	N	N	N	Description	\N	50	2021-05-08 20:18:52.834	0	col-md-6 mb-3	0	\N
0	408	\N	0	46	699	2021-05-06 21:09:48.569441	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2021-05-06 21:09:48.569441	0	col-md-6 mb-3	0	\N
0	409	\N	0	46	700	2021-05-06 21:10:45.447937	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2021-05-06 21:10:45.447937	0	col-md-6 mb-3	0	\N
0	410	\N	0	46	706	2021-05-06 21:11:14.182221	0	\N	\N	\N	Y	Y	Y	Y	N	N	Value	\N	30	2021-05-06 21:11:14.182221	0	col-md-6 mb-3	0	\N
0	422	\N	0	47	750	2021-05-08 20:20:40.72587	0	\N	\N	\N	Y	Y	Y	N	N	Y	DynamicValidation	\N	60	2021-05-08 20:20:40.72587	0	col-md-6 mb-3	0	\N
0	412	\N	0	46	710	2021-05-06 21:12:41.04997	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Extension	\N	40	2021-05-06 21:12:41.04997	0	col-md-6 mb-3	0	\N
0	411	\N	0	46	709	2021-05-06 21:11:39.723412	0	\N	\N	\N	Y	Y	Y	N	N	N	Procedure Name	\N	50	2021-05-06 21:12:49.241	0	col-md-6 mb-3	0	\N
0	413	\N	0	46	711	2021-05-06 21:13:08.024553	0	\N	\N	\N	Y	Y	Y	\N	N	Y	Scripting	\N	60	2021-05-06 21:13:38.605	0	col-md-6 mb-3	0	\N
0	414	\N	0	46	707	2021-05-06 21:14:19.058597	0	\N	\N	\N	Y	Y	Y	\N	N	N	Description	\N	70	2021-05-06 21:14:19.058597	0	col-md-12 mb-3	0	\N
0	415	\N	0	46	708	2021-05-06 21:14:33.63022	0	\N	\N	\N	Y	Y	Y	\N	N	N	Help	\N	70	2021-05-06 21:14:33.63022	0	col-md-12 mb-3	0	\N
0	398	\N	0	45	714	2021-05-06 20:48:38.741196	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	N	N	Client	\N	10	2021-05-06 21:15:24.915	0	col-md-6 mb-3	0	\N
0	399	\N	0	45	715	2021-05-06 20:49:07.029524	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2021-05-06 21:15:29.736	0	col-md-6 mb-3	0	\N
0	407	\N	0	45	716	2021-05-06 20:53:08.82655	0	Y	\N	\N	Y	Y	Y	Y	N	N	Active	\N	100	2021-05-06 21:15:43.947	0	col-md-6 mb-3	0	\N
0	406	\N	0	45	728	2021-05-06 20:52:12.447015	0	N	\N	\N	Y	Y	Y	Y	N	Y	Record Action	\N	90	2021-05-06 21:15:52.648	0	col-md-6 mb-3	0	\N
0	416	\N	0	46	701	2021-05-06 21:14:59.62156	0	Y	\N	\N	Y	Y	Y	Y	N	N	Active	\N	80	2021-05-06 21:19:37.169	0	col-md-6 mb-3	0	\N
0	417	\N	0	2	109	2021-05-06 21:37:06.476221	0	\N	\N	\N	Y	Y	Y	\N	Y	N	CreatedBy	\N	110	2021-05-06 21:37:06.476221	0	col-md-6 mb-3	0	\N
0	418	\N	0	47	730	2021-05-08 20:15:20.248865	0	@#AD_Client_ID@	\N	\N	Y	Y	Y	Y	Y	N	Client	\N	10	2021-05-08 20:15:20.248865	0	col-md-6 mb-3	0	\N
0	419	\N	0	47	733	2021-05-08 20:15:58.466503	0	@#AD_Org_ID@	\N	\N	Y	Y	Y	Y	N	Y	Organization	\N	20	2021-05-08 20:15:58.466503	0	col-md-6 mb-3	0	\N
0	423	\N	0	47	738	2021-05-08 20:20:58.714828	0	\N	\N	\N	Y	Y	Y	N	N	N	Comment/Help	\N	70	2021-05-08 20:20:58.714828	0	col-md-6 mb-3	0	\N
0	424	\N	0	47	736	2021-05-08 20:21:16.795369	0	\N	\N	\N	Y	Y	Y	N	N	Y	Default Value	\N	80	2021-05-08 20:21:16.795369	0	col-md-6 mb-3	0	\N
0	426	\N	0	47	743	2021-05-08 20:21:55.75002	0	\N	\N	\N	Y	Y	Y	N	N	N	Placeholder	\N	100	2021-05-08 20:21:55.75002	0	col-md-6 mb-3	0	\N
0	427	\N	0	47	748	2021-05-08 20:22:09.535701	0	col-md-6 mb-3	\N	\N	Y	Y	Y	N	N	Y	Class	\N	110	2021-05-08 20:22:09.535701	0	col-md-6 mb-3	0	\N
0	428	\N	0	47	747	2021-05-08 20:23:00.397985	0	col-md-6 mb-3	\N	\N	Y	Y	Y	Y	Y	N	Process	\N	22	2021-05-08 20:23:08.54	0	col-md-6 mb-3	0	\N
0	429	\N	0	47	744	2021-05-08 20:23:29.053321	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Sequence	\N	29	2021-05-08 20:23:29.053321	0	col-md-6 mb-3	0	\N
0	430	\N	0	47	742	2021-05-08 20:24:43.439554	0	\N	\N	\N	Y	Y	Y	Y	N	N	Label	\N	30	2021-05-08 21:05:26.906	0	col-md-6 mb-3	0	\N
0	431	\N	0	47	751	2021-05-08 21:06:04.005001	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Param Name	\N	120	2021-05-08 21:06:04.005001	0	col-md-6 mb-3	0	\N
0	425	\N	0	47	740	2021-05-08 20:21:33.019254	0	Y	\N	\N	Y	Y	Y	N	N	N	Mandatory	\N	130	2021-05-08 21:07:57.321	0	col-md-6 mb-3	0	\N
0	432	\N	0	47	741	2021-05-08 21:08:17.357425	0	N	\N	\N	Y	Y	Y	N	N	N	Same Line	\N	140	2021-05-08 21:08:17.357425	0	col-md-6 mb-3	0	\N
0	434	\N	0	47	752	2021-05-08 21:14:38.209293	0	\N	\N	\N	Y	Y	Y	Y	N	Y	Reference	\N	125	2021-05-08 21:16:44.178	0	col-md-6 mb-3	0	ValidationType eq 'D'
0	435	\N	0	47	753	2021-05-08 21:15:03.048862	0	\N	\N	\N	Y	Y	Y	N	N	N	Reference Value	\N	128	2021-05-08 21:16:50.804	0	col-md-6 mb-3	0	ValidationType eq 'T' or ValidationType eq 'L'
0	436	\N	0	37	754	2021-05-11 11:12:22.022567	0	\N	\N	\N	Y	Y	Y	N	N	N	Role	\N	45	2021-05-11 11:12:22.022567	0	col-md-6	0	\N
0	433	\N	0	47	739	2021-05-08 21:08:36.672116	0	Y	\N	\N	Y	Y	Y	Y	N	N	Active	\N	150	2021-05-11 22:18:54.68	0	col-md-6 mb-3	0	\N
\.


--
-- Data for Name: ad_jobdefinition; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_jobdefinition (ad_jobdefinition_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, help, ad_process_id, ad_jobdefinition_uu, procedurename, ad_scripting_id) FROM stdin;
7	0	0	Y	2021-04-28 12:28:57.528005	0	2021-04-28 12:28:57.528005	0	Teste	Teste	\N	\N	d29b67f5-4306-42bb-869f-72d9bba6002a	teste	7
\.


--
-- Data for Name: ad_language; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_language (ad_language, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, languageiso, countrycode, isbaselanguage, issystemlanguage, ad_language_id, isdecimalpoint, datepattern, timepattern, ad_language_uu) FROM stdin;
pt_BR	0	0	Y	2020-03-14 17:31:44.954382	0	2020-03-14 17:31:44.954382	0	Portuguese (Brazil)	pt	BR	N	N	174	N	dd/MM/yyyy		5fc37129-3221-4a10-b798-c7320d120dcb
en_US	0	0	Y	2020-03-14 17:31:54.365573	0	2020-03-14 17:31:54.365573	0	English (USA)	en	US	Y	Y	192	Y	MM/dd/yyyy		00606a20-8f76-42e1-a0fb-0b563d43f36f
ar_AE	0	0	Y	2003-08-06 18:41:25	0	2000-01-02 00:00:00	0	Arabic (United Arab Emirates)	ar	AE	N	N	100	Y	\N	\N	\N
ar_BH	0	0	Y	2003-08-06 18:41:26	0	2000-01-02 00:00:00	0	Arabic (Bahrain)	ar	BH	N	N	101	Y	\N	\N	\N
ar_DZ	0	0	Y	2003-08-06 18:41:59	0	2000-01-02 00:00:00	0	Arabic (Algeria)	ar	DZ	N	N	102	Y	\N	\N	\N
ar_EG	0	0	Y	2003-08-06 18:41:59	0	2000-01-02 00:00:00	0	Arabic (Egypt)	ar	EG	N	N	103	Y	\N	\N	\N
ar_IQ	0	0	Y	2003-08-06 18:41:59	0	2000-01-02 00:00:00	0	Arabic (Iraq)	ar	IQ	N	N	104	Y	\N	\N	\N
ar_JO	0	0	Y	2003-08-06 18:41:59	0	2000-01-02 00:00:00	0	Arabic (Jordan)	ar	JO	N	N	105	Y	\N	\N	\N
ar_KW	0	0	Y	2003-08-06 18:41:59	0	2000-01-02 00:00:00	0	Arabic (Kuwait)	ar	KW	N	N	106	Y	\N	\N	\N
ar_LB	0	0	Y	2003-08-06 18:41:59	0	2000-01-02 00:00:00	0	Arabic (Lebanon)	ar	LB	N	N	107	Y	\N	\N	\N
ar_LY	0	0	Y	2003-08-06 18:42:00	0	2000-01-02 00:00:00	0	Arabic (Libya)	ar	LY	N	N	108	Y	\N	\N	\N
ar_MA	0	0	Y	2003-08-06 18:42:00	0	2000-01-02 00:00:00	0	Arabic (Morocco)	ar	MA	N	N	109	Y	\N	\N	\N
ar_OM	0	0	Y	2003-08-06 18:42:00	0	2000-01-02 00:00:00	0	Arabic (Oman)	ar	OM	N	N	110	Y	\N	\N	\N
ar_QA	0	0	Y	2003-08-06 18:42:00	0	2000-01-02 00:00:00	0	Arabic (Qatar)	ar	QA	N	N	111	Y	\N	\N	\N
ar_SA	0	0	Y	2003-08-06 18:42:00	0	2000-01-02 00:00:00	0	Arabic (Saudi Arabia)	ar	SA	N	N	112	Y	\N	\N	\N
ar_SD	0	0	Y	2003-08-06 18:42:00	0	2000-01-02 00:00:00	0	Arabic (Sudan)	ar	SD	N	N	113	Y	\N	\N	\N
ar_SY	0	0	Y	2003-08-06 18:42:00	0	2000-01-02 00:00:00	0	Arabic (Syria)	ar	SY	N	N	114	Y	\N	\N	\N
ar_TN	0	0	Y	2003-08-06 18:42:01	0	2000-01-02 00:00:00	0	Arabic (Tunisia)	ar	TN	N	N	115	Y	\N	\N	\N
ar_YE	0	0	Y	2003-08-06 18:42:01	0	2000-01-02 00:00:00	0	Arabic (Yemen)	ar	YE	N	N	116	Y	\N	\N	\N
be_BY	0	0	Y	2003-08-06 18:42:01	0	2000-01-02 00:00:00	0	Byelorussian (Belarus)	be	BY	N	N	117	Y	\N	\N	\N
bg_BG	0	0	Y	2003-08-06 18:42:01	0	2000-01-02 00:00:00	0	Bulgarian (Bulgaria)	bg	BG	N	N	118	Y	\N	\N	\N
ca_ES	0	0	Y	2003-08-06 18:42:01	0	2000-01-02 00:00:00	0	Catalan (Spain)	ca	ES	N	N	119	Y	\N	\N	\N
cs_CZ	0	0	Y	2003-08-06 18:42:01	0	2000-01-02 00:00:00	0	Czech (Czech Republic)	cs	CZ	N	N	120	Y	\N	\N	\N
da_DK	0	0	Y	2003-08-06 18:42:01	0	2000-01-02 00:00:00	0	Danish (Denmark)	da	DK	N	N	121	Y	\N	\N	\N
de_AT	0	0	Y	2003-08-06 18:42:02	0	2000-01-02 00:00:00	0	German (Austria)	de	AT	N	N	122	Y	\N	\N	\N
de_CH	0	0	Y	2003-08-06 18:42:02	0	2000-01-02 00:00:00	0	German (Switzerland)	de	CH	N	N	123	Y	\N	\N	\N
de_DE	0	0	Y	2001-12-18 21:14:24	0	2000-01-02 00:00:00	0	German (Germany)	de	DE	N	N	191	Y	\N	\N	\N
de_LU	0	0	Y	2003-08-06 18:42:02	0	2000-01-02 00:00:00	0	German (Luxembourg)	de	LU	N	N	124	Y	\N	\N	\N
el_CY	0	0	Y	2010-03-18 18:51:52	100	2010-03-18 18:51:52	100	Greek (Cyprus)	el	CY	N	N	50004	Y	\N	\N	\N
el_GR	0	0	Y	2003-08-06 18:42:02	0	2000-01-02 00:00:00	0	Greek (Greece)	el	GR	N	N	125	Y	\N	\N	\N
en_AU	0	0	Y	2003-08-06 18:42:02	0	2000-01-02 00:00:00	0	English (Australia)	en	AU	N	N	126	Y	\N	\N	\N
en_CA	0	0	Y	2003-08-06 18:42:02	0	2000-01-02 00:00:00	0	English (Canada)	en	CA	N	N	127	Y	\N	\N	\N
en_GB	0	0	Y	2003-08-06 18:42:03	0	2000-01-02 00:00:00	0	English (United Kingdom)	en	GB	N	N	128	Y	\N	\N	\N
en_IE	0	0	Y	2003-08-06 18:42:03	0	2000-01-02 00:00:00	0	English (Ireland)	en	IE	N	N	129	Y	\N	\N	\N
en_IN	0	0	Y	2003-08-06 18:42:03	0	2000-01-02 00:00:00	0	English (India)	en	IN	N	N	130	Y	\N	\N	\N
en_MT	0	0	Y	2010-03-18 18:52:14	100	2010-03-18 18:52:14	100	English (Malta)	en	MT	N	N	50005	Y	\N	\N	\N
en_NZ	0	0	Y	2003-08-06 18:42:03	0	2000-01-02 00:00:00	0	English (New Zealand)	en	NZ	N	N	131	Y	\N	\N	\N
en_PH	0	0	Y	2010-03-18 18:52:33	100	2010-03-18 18:52:33	100	English (Philippines)	en	PH	N	N	50006	Y	\N	\N	\N
en_SG	0	0	Y	2010-03-18 18:52:48	100	2010-03-18 18:52:48	100	English (Singapore)	en	SG	N	N	50007	Y	\N	\N	\N
en_ZA	0	0	Y	2003-08-06 18:42:03	0	2000-01-02 00:00:00	0	English (South Africa)	en	ZA	N	N	132	Y	\N	\N	\N
es_AR	0	0	Y	2003-08-06 18:42:03	0	2000-01-02 00:00:00	0	Spanish (Argentina)	es	AR	N	N	133	Y	\N	\N	\N
es_BO	0	0	Y	2003-08-06 18:42:03	0	2000-01-02 00:00:00	0	Spanish (Bolivia)	es	BO	N	N	134	Y	\N	\N	\N
es_CL	0	0	Y	2003-08-06 18:42:04	0	2000-01-02 00:00:00	0	Spanish (Chile)	es	CL	N	N	135	Y	\N	\N	\N
es_CO	0	0	Y	2003-08-06 18:42:04	0	2000-01-02 00:00:00	0	Spanish (Colombia)	es	CO	N	N	136	Y	\N	\N	\N
es_CR	0	0	Y	2003-08-06 18:42:04	0	2000-01-02 00:00:00	0	Spanish (Costa Rica)	es	CR	N	N	137	Y	\N	\N	\N
es_DO	0	0	Y	2003-08-06 18:42:04	0	2000-01-02 00:00:00	0	Spanish (Dominican Republic)	es	DO	N	N	138	Y	\N	\N	\N
es_EC	0	0	Y	2003-08-06 18:42:04	0	2000-01-02 00:00:00	0	Spanish (Ecuador)	es	EC	N	N	139	Y	\N	\N	\N
es_ES	0	0	Y	2003-08-06 18:42:04	0	2000-01-02 00:00:00	0	Spanish (Spain)	es	ES	N	N	140	Y	\N	\N	\N
es_GT	0	0	Y	2003-08-06 18:42:04	0	2000-01-02 00:00:00	0	Spanish (Guatemala)	es	GT	N	N	141	Y	\N	\N	\N
es_HN	0	0	Y	2003-08-06 18:42:04	0	2000-01-02 00:00:00	0	Spanish (Honduras)	es	HN	N	N	142	Y	\N	\N	\N
es_MX	0	0	Y	2003-08-06 18:42:05	0	2009-01-21 11:11:09	100	Spanish (Mexico)	es	MX	N	N	143	Y	\N	\N	\N
es_NI	0	0	Y	2003-08-06 18:42:05	0	2000-01-02 00:00:00	0	Spanish (Nicaragua)	es	NI	N	N	144	Y	\N	\N	\N
es_PA	0	0	Y	2003-08-06 18:42:05	0	2000-01-02 00:00:00	0	Spanish (Panama)	es	PA	N	N	145	Y	\N	\N	\N
es_PE	0	0	Y	2003-08-06 18:42:05	0	2000-01-02 00:00:00	0	Spanish (Peru)	es	PE	N	N	146	Y	\N	\N	\N
es_PR	0	0	Y	2003-08-06 18:42:05	0	2000-01-02 00:00:00	0	Spanish (Puerto Rico)	es	PR	N	N	147	Y	\N	\N	\N
es_PY	0	0	Y	2003-08-06 18:42:05	0	2000-01-02 00:00:00	0	Spanish (Paraguay)	es	PY	N	N	148	Y	\N	\N	\N
es_SV	0	0	Y	2003-08-06 18:42:05	0	2000-01-02 00:00:00	0	Spanish (El Salvador)	es	SV	N	N	149	Y	\N	\N	\N
es_US	0	0	Y	2010-03-18 18:53:06	100	2010-03-18 18:53:06	100	Spanish (USA)	es	US	N	N	50008	Y	\N	\N	\N
es_UY	0	0	Y	2003-08-06 18:42:06	0	2000-01-02 00:00:00	0	Spanish (Uruguay)	es	UY	N	N	150	Y	\N	\N	\N
es_VE	0	0	Y	2003-08-06 18:42:06	0	2000-01-02 00:00:00	0	Spanish (Venezuela)	es	VE	N	N	151	Y	\N	\N	\N
et_EE	0	0	Y	2003-08-06 18:42:06	0	2000-01-02 00:00:00	0	Estonian (Estonia)	et	EE	N	N	152	Y	\N	\N	\N
fa_IR	0	0	Y	2003-08-24 20:54:59	0	2000-01-02 00:00:00	0	Farsi (Iran)	fa	IR	N	N	193	Y	\N	\N	\N
fi_FI	0	0	Y	2003-08-06 18:42:06	0	2000-01-02 00:00:00	0	Finnish (Finland)	fi	FI	N	N	153	Y	\N	\N	\N
fr_BE	0	0	Y	2003-08-06 18:42:06	0	2000-01-02 00:00:00	0	French (Belgium)	fr	BE	N	N	154	Y	\N	\N	\N
fr_CA	0	0	Y	2003-08-06 18:42:06	0	2000-01-02 00:00:00	0	French (Canada)	fr	CA	N	N	155	Y	\N	\N	\N
fr_CH	0	0	Y	2003-08-06 18:42:06	0	2000-01-02 00:00:00	0	French (Switzerland)	fr	CH	N	N	156	Y	\N	\N	\N
fr_FR	0	0	Y	2002-07-27 12:00:54	0	2000-01-02 00:00:00	0	French (France)	fr	FR	N	N	190	Y	\N	\N	\N
fr_LU	0	0	Y	2003-08-06 18:42:07	0	2000-01-02 00:00:00	0	French (Luxembourg)	fr	LU	N	N	157	Y	\N	\N	\N
ga_IE	0	0	Y	2010-03-18 18:53:22	100	2010-03-18 18:53:22	100	Irish (Ireland)	ga	IE	N	N	50009	Y	\N	\N	\N
hi_IN	0	0	Y	2003-08-06 18:42:07	0	2000-01-02 00:00:00	0	Hindi (India)	hi	IN	N	N	158	Y	\N	\N	\N
hr_HR	0	0	Y	2003-08-06 18:42:07	0	2000-01-02 00:00:00	0	Croatian (Croatia)	hr	HR	N	N	159	Y	\N	\N	\N
hu_HU	0	0	Y	2003-08-06 18:42:07	0	2000-01-02 00:00:00	0	Hungarian (Hungary)	hu	HU	N	N	160	Y	\N	\N	\N
in_ID	0	0	Y	2010-03-18 18:53:36	100	2010-03-18 18:53:36	100	Indonesian (Indonesia)	in	ID	N	N	50010	Y	\N	\N	\N
is_IS	0	0	Y	2003-08-06 18:42:07	0	2000-01-02 00:00:00	0	Icelandic (Iceland)	is	IS	N	N	161	Y	\N	\N	\N
it_CH	0	0	Y	2003-08-06 18:42:07	0	2000-01-02 00:00:00	0	Italian (Switzerland)	it	CH	N	N	162	Y	\N	\N	\N
it_IT	0	0	Y	2003-08-06 18:42:07	0	2000-01-02 00:00:00	0	Italian (Italy)	it	IT	N	N	163	Y	\N	\N	\N
iw_IL	0	0	Y	2003-08-06 18:42:08	0	2000-01-02 00:00:00	0	Hebrew (Israel)	iw	IL	N	N	164	Y	\N	\N	\N
ja_JP	0	0	Y	2003-08-06 18:42:08	0	2000-01-02 00:00:00	0	Japanese (Japan)	ja	JP	N	N	165	Y	\N	\N	\N
ko_KR	0	0	Y	2003-08-06 18:42:08	0	2000-01-02 00:00:00	0	Korean (South Korea)	ko	KR	N	N	166	Y	\N	\N	\N
lt_LT	0	0	Y	2003-08-06 18:42:08	0	2000-01-02 00:00:00	0	Lithuanian (Lithuania)	lt	LT	N	N	167	Y	\N	\N	\N
lv_LV	0	0	Y	2003-08-06 18:42:10	0	2000-01-02 00:00:00	0	Latvian (Lettish) (Latvia)	lv	LV	N	N	168	Y	\N	\N	\N
mk_MK	0	0	Y	2003-08-06 18:42:10	0	2000-01-02 00:00:00	0	Macedonian (Macedonia)	mk	MK	N	N	169	Y	\N	\N	\N
ms_MY	0	0	Y	2010-03-18 18:51:35	100	2010-03-18 18:51:35	100	Malay (Malaysia)	ms	MY	N	N	50003	Y	\N	\N	\N
mt_MT	0	0	Y	2010-03-18 18:53:50	100	2010-03-18 18:53:50	100	Maltese (Malta)	mt	MT	N	N	50011	Y	\N	\N	\N
nl_BE	0	0	Y	2003-08-06 18:42:10	0	2000-01-02 00:00:00	0	Dutch (Belgium)	nl	BE	N	N	170	Y	\N	\N	\N
nl_NL	0	0	Y	2003-08-06 18:42:10	0	2000-01-02 00:00:00	0	Dutch (Netherlands)	nl	NL	N	N	171	Y	\N	\N	\N
no_NO	0	0	Y	2003-08-06 18:42:10	0	2000-01-02 00:00:00	0	Norwegian (Norway)	no	NO	N	N	172	Y	\N	\N	\N
pl_PL	0	0	Y	2003-08-06 18:42:10	0	2000-01-02 00:00:00	0	Polish (Poland)	pl	PL	N	N	173	Y	\N	\N	\N
pt_PT	0	0	Y	2003-08-06 18:42:11	0	2000-01-02 00:00:00	0	Portuguese (Portugal)	pt	PT	N	N	175	Y	\N	\N	\N
ro_RO	0	0	Y	2003-08-06 18:42:11	0	2000-01-02 00:00:00	0	Romanian (Romania)	ro	RO	N	N	176	Y	\N	\N	\N
ru_RU	0	0	Y	2003-08-06 18:42:11	0	2000-01-02 00:00:00	0	Russian (Russia)	ru	RU	N	N	177	Y	\N	\N	\N
sh_YU	0	0	Y	2003-08-06 18:42:11	0	2000-01-02 00:00:00	0	Serbo-Croatian (Yugoslavia)	sh	YU	N	N	178	Y	\N	\N	\N
sk_SK	0	0	Y	2003-08-06 18:42:11	0	2000-01-02 00:00:00	0	Slovak (Slovakia)	sk	SK	N	N	179	Y	\N	\N	\N
sl_SI	0	0	Y	2003-08-06 18:42:11	0	2000-01-02 00:00:00	0	Slovenian (Slovenia)	sl	SI	N	N	180	Y	\N	\N	\N
sq_AL	0	0	Y	2003-08-06 18:42:12	0	2000-01-02 00:00:00	0	Albanian (Albania)	sq	AL	N	N	181	Y	\N	\N	\N
sr_BA	0	0	Y	2010-03-18 18:55:52	100	2010-03-18 18:55:52	100	Serbian (Bosnia and Herzegovina)	sr	BA	N	N	50012	Y	\N	\N	\N
sr_CS	0	0	Y	2010-03-18 18:56:05	100	2010-03-18 18:56:05	100	Serbian (Serbia and Montenegro)	sr	CS	N	N	50013	Y	\N	\N	\N
sr_ME	0	0	Y	2010-03-18 18:56:16	100	2010-03-18 18:56:16	100	Serbian (Montenegro)	sr	ME	N	N	50014	Y	\N	\N	\N
sr_RS	0	0	Y	2010-03-18 18:56:29	100	2010-03-18 18:56:29	100	Serbian (Serbia)	sr	RS	N	N	50015	Y	\N	\N	\N
sr_YU	0	0	Y	2003-08-06 18:42:12	0	2000-01-02 00:00:00	0	Serbian (Yugoslavia)	sr	YU	N	N	182	Y	\N	\N	\N
sv_SE	0	0	Y	2003-08-06 18:42:12	0	2000-01-02 00:00:00	0	Swedish (Sweden)	sv	SE	N	N	183	Y	\N	\N	\N
th_TH	0	0	Y	2003-08-06 18:42:12	0	2000-01-02 00:00:00	0	Thai (Thailand)	th	TH	N	N	184	Y	\N	\N	\N
tr_TR	0	0	Y	2003-08-06 18:42:12	0	2000-01-02 00:00:00	0	Turkish (Turkey)	tr	TR	N	N	185	Y	\N	\N	\N
uk_UA	0	0	Y	2003-08-06 18:42:12	0	2000-01-02 00:00:00	0	Ukrainian (Ukraine)	uk	UA	N	N	186	Y	\N	\N	\N
vi_VN	0	0	Y	2005-07-25 10:22:01	100	2005-07-25 10:22:01	100	Vietnamese	vi	VN	N	N	194	N	\N	\N	\N
zh_CN	0	0	Y	2003-08-06 18:42:12	0	2000-01-02 00:00:00	0	Chinese (China)	zh	CN	N	N	187	Y	\N	\N	\N
zh_HK	0	0	Y	2003-08-06 18:42:13	0	2000-01-02 00:00:00	0	Chinese (Hong Kong)	zh	HK	N	N	188	Y	\N	\N	\N
zh_SG	0	0	Y	2010-03-18 18:56:41	100	2010-03-18 18:56:41	100	Chinese (Singapore)	zh	SG	N	N	50016	Y	\N	\N	\N
zh_TW	0	0	Y	2003-08-06 18:42:13	0	2000-01-02 00:00:00	0	Chinese (Taiwan)	zh	TW	N	N	189	Y	\N	\N	\N
\.

--
-- Data for Name: ad_mediafolder; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_mediafolder (ad_mediafolder_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, method, attributes, ad_mediafolder_uu, isinternalstorage, issecurityaccess) FROM stdin;
1	0	0	Y	2020-03-10 02:11:01.084768	0	2020-03-10 02:11:01.084768	0	empty	empty		4c5d3b49-96f4-49f4-b5df-c8cb19f14464	Y	Y
\.


--
-- Data for Name: ad_mediaformat; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_mediaformat (ad_mediaformat_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, extension, mimetype, description, ad_mediaformat_uu) FROM stdin;
1	0	0	Y	2020-03-08 00:31:49.659621	0	2020-03-08 00:31:49.659621	0	.aac	audio/aac	AAC audio	1be5c56b-47be-4551-a056-68933f1eef6a
2	0	0	Y	2020-03-08 00:31:49.674645	0	2020-03-08 00:31:49.674645	0	.abw	application/x-abiword	AbiWord document	d5d34fbd-1f3f-48f0-939b-f7fe36ce5731
3	0	0	Y	2020-03-08 00:31:49.681454	0	2020-03-08 00:31:49.681454	0	.arc	application/x-freearc	Archive document (multiple files embedded)	0e3268b2-4059-4c18-a1bc-ca521e30f7e5
4	0	0	Y	2020-03-08 00:31:49.687263	0	2020-03-08 00:31:49.687263	0	.avi	video/x-msvideo	AVI: Audio Video Interleave	ff3a7ed2-3f42-4d68-a273-152c8f1c8b4f
5	0	0	Y	2020-03-08 00:31:49.691795	0	2020-03-08 00:31:49.691795	0	.azw	application/vnd.amazon.ebook	Amazon Kindle eBook format	31d65b4c-be43-43d3-abc0-355b719c5a39
6	0	0	Y	2020-03-08 00:31:49.695734	0	2020-03-08 00:31:49.695734	0	.bin	application/octet-stream	Any kind of binary data	712cf6b3-2626-4848-8a1a-84c2489249c6
7	0	0	Y	2020-03-08 00:31:49.699729	0	2020-03-08 00:31:49.699729	0	.bmp	image/bmp	Windows OS/2 Bitmap Graphics	30ebbea4-a6cb-4f08-8afc-7ec92e0f19a8
8	0	0	Y	2020-03-08 00:31:49.703166	0	2020-03-08 00:31:49.703166	0	.bz	application/x-bzip	BZip archive	2ff6de6b-bdc7-4420-be54-40de577d76a5
9	0	0	Y	2020-03-08 00:31:49.706084	0	2020-03-08 00:31:49.706084	0	.bz2	 application/x-bzip2	BZip2 archive	bf5f19a6-2440-4bf2-9f1a-1e1ea572fa2a
10	0	0	Y	2020-03-08 00:31:49.708645	0	2020-03-08 00:31:49.708645	0	.csh	 application/x-csh	C-Shell script	a6fdc800-1e94-4941-a263-433b0fdf915b
11	0	0	Y	2020-03-08 00:31:49.711343	0	2020-03-08 00:31:49.711343	0	.css	 text/css	Cascading Style Sheets (CSS)	64209810-a25c-4ef3-9d97-6a1be60ac8c6
12	0	0	Y	2020-03-08 00:31:49.71383	0	2020-03-08 00:31:49.71383	0	.csv	 text/csv	Comma-separated values (CSV)	0d10523c-196e-4a6d-aef1-acb4867f5c23
13	0	0	Y	2020-03-08 00:31:49.716421	0	2020-03-08 00:31:49.716421	0	.doc	 application/msword	Microsoft Word	3fc631cd-21dd-4fbe-add5-795964d446c8
14	0	0	Y	2020-03-08 00:31:49.71957	0	2020-03-08 00:31:49.71957	0	.docx	application/vnd.openxmlformats-officedocument.wordprocessingml.document	Microsoft Word (OpenXML)	e5901faa-a6f6-4de7-8509-027fdaaaf849
15	0	0	Y	2020-03-08 00:31:49.722747	0	2020-03-08 00:31:49.722747	0	.eot	 application/vnd.ms-fontobject	MS Embedded OpenType fonts	0d72ef8a-36a7-45e3-985e-6c7de2e04af9
16	0	0	Y	2020-03-08 00:31:49.726452	0	2020-03-08 00:31:49.726452	0	.epub	application/epub+zip	Electronic publication (EPUB)	ca3c88bf-cfa3-44f0-80fc-c2152c453fec
17	0	0	Y	2020-03-08 00:31:49.729666	0	2020-03-08 00:31:49.729666	0	.gz	application/gzip	GZip Compressed Archive	2a1023c7-fa75-43bb-9c45-bb878cd24f4c
18	0	0	Y	2020-03-08 00:31:49.732325	0	2020-03-08 00:31:49.732325	0	.gif	image/gif	Graphics Interchange Format (GIF)	f3ab6342-6b63-4a0f-944c-b39fe6262fd9
19	0	0	Y	2020-03-08 00:31:49.735374	0	2020-03-08 00:31:49.735374	0	.html	text/html	HyperText Markup Language (HTML)	d5ecbefb-0b70-4b98-a37b-5c5df5d963c5
20	0	0	Y	2020-03-08 00:31:49.738173	0	2020-03-08 00:31:49.738173	0	.ico	image/vnd.microsoft.icon	Icon format	bde51092-e31f-45f0-ac6e-7d05d7c40bea
21	0	0	Y	2020-03-08 00:31:49.741466	0	2020-03-08 00:31:49.741466	0	.ics	text/calendar	iCalendar format	e069fb69-8f60-4029-ad4f-6bc9f0d69596
22	0	0	Y	2020-03-08 00:31:49.744954	0	2020-03-08 00:31:49.744954	0	.jar	application/java-archive	Java Archive (JAR)	6f2a29e8-6d48-4b0a-840e-02184210e644
23	0	0	Y	2020-03-08 00:31:49.748862	0	2020-03-08 00:31:49.748862	0	.jpep;	image/jpeg	JPEG images	f42f7509-485c-4092-8907-2cb1a89ac780
24	0	0	Y	2020-03-08 00:31:49.752291	0	2020-03-08 00:31:49.752291	0	.js	text/javascript	JavaScript	8ef0a886-b5e8-4cbc-9d40-47de84523b2a
25	0	0	Y	2020-03-08 00:31:49.755475	0	2020-03-08 00:31:49.755475	0	.json	application/json	JSON format	55aeae33-8d3f-4eb4-b4dd-f9436a343cd3
26	0	0	Y	2020-03-08 00:31:49.758784	0	2020-03-08 00:31:49.758784	0	.jsonld	application/ld+json	JSON-LD format	f52f7cab-ee9c-4f4d-85b1-ce2b5cd75392
27	0	0	Y	2020-03-08 00:31:49.761712	0	2020-03-08 00:31:49.761712	0	.mid	audio/midi audio/x-midi	Musical Instrument Digital Interface (MIDI)	b679fd70-301b-4c71-9230-81008def6bf6
28	0	0	Y	2020-03-08 00:31:49.764142	0	2020-03-08 00:31:49.764142	0	.mjs	text/javascript	JavaScript module	e5c58cda-1f0c-4c6f-be32-5d8f3eafd7df
29	0	0	Y	2020-03-08 00:31:49.766514	0	2020-03-08 00:31:49.766514	0	.mp3	audio/mpeg	MP3 audio	ac535d3d-4ba0-413e-b08c-3dd0fdc9701c
30	0	0	Y	2020-03-08 00:31:49.768639	0	2020-03-08 00:31:49.768639	0	.mpeg	video/mpeg	MPEG Video	fde3f733-4719-4dc8-b71f-07909141aa4c
31	0	0	Y	2020-03-08 00:31:49.770905	0	2020-03-08 00:31:49.770905	0	.mpkg	application/vnd.apple.installer+xml	Apple Installer Package	cee210b4-1e0a-4fbe-ae7d-1d47d62e0b73
32	0	0	Y	2020-03-08 00:31:49.773482	0	2020-03-08 00:31:49.773482	0	.odp	application/vnd.oasis.opendocument.presentation	OpenDocument presentation document	3b19651f-1632-4c71-9cf5-f188b2bd6f8d
33	0	0	Y	2020-03-08 00:31:49.776242	0	2020-03-08 00:31:49.776242	0	.ods	application/vnd.oasis.opendocument.spreadsheet	OpenDocument spreadsheet document	63dfcef0-8df9-4900-ab0f-0f11d902bf42
34	0	0	Y	2020-03-08 00:31:49.778633	0	2020-03-08 00:31:49.778633	0	.odt	application/vnd.oasis.opendocument.text	OpenDocument text document	17169670-61a8-479c-8b68-c82ad52d6f15
35	0	0	Y	2020-03-08 00:31:49.780707	0	2020-03-08 00:31:49.780707	0	.oga	audio/ogg	OGG audio	f14563a4-e5b6-495e-b7e4-b5de445e3bb3
36	0	0	Y	2020-03-08 00:31:49.782868	0	2020-03-08 00:31:49.782868	0	.ogv	video/ogg	OGG video	5f236420-caf1-4b0d-8b64-585912e54155
37	0	0	Y	2020-03-08 00:31:49.785064	0	2020-03-08 00:31:49.785064	0	.ogx	application/ogg	OGG	85cde2bd-5749-4014-a909-70e5370dd6c0
38	0	0	Y	2020-03-08 00:31:49.787647	0	2020-03-08 00:31:49.787647	0	.opus	audio/opus	Opus audio	0db1278f-1e32-48fc-afb6-03371d67cbfd
39	0	0	Y	2020-03-08 00:31:49.789668	0	2020-03-08 00:31:49.789668	0	.otf	font/otf	OpenType font	2bbf17b4-4fce-4110-83a0-f9d64bb4bded
40	0	0	Y	2020-03-08 00:31:49.791858	0	2020-03-08 00:31:49.791858	0	.png	image/png	Portable Network Graphics	c69c75e4-4842-4846-97c7-87942ac0484b
41	0	0	Y	2020-03-08 00:31:49.794133	0	2020-03-08 00:31:49.794133	0	.pdf	application/pdf	Adobe Portable Document Format (PDF)	9cbf0ea4-bafd-43c1-ac74-73e83a7f86e0
42	0	0	Y	2020-03-08 00:31:49.796199	0	2020-03-08 00:31:49.796199	0	.php	application/php	Hypertext Preprocessor (Personal Home Page)	dc952a66-c543-442f-a36e-7ac039a9eae1
43	0	0	Y	2020-03-08 00:31:49.798191	0	2020-03-08 00:31:49.798191	0	.ppt	application/vnd.ms-powerpoint	Microsoft PowerPoint	6e245204-f54e-4b42-a72c-f59f6b4e82ec
44	0	0	Y	2020-03-08 00:31:49.800817	0	2020-03-08 00:31:49.800817	0	.pptx	application/vnd.openxmlformats-officedocument.presentationml.presentation	Microsoft PowerPoint (OpenXML)	d4a48f53-9cf0-4ae5-8d46-109f518bd745
45	0	0	Y	2020-03-08 00:31:49.803104	0	2020-03-08 00:31:49.803104	0	.rar	application/vnd.rar	RAR archive	e3c7d7e1-c449-4419-b1f2-e6f872bff45d
46	0	0	Y	2020-03-08 00:31:49.805845	0	2020-03-08 00:31:49.805845	0	.rtf	application/rtf	Rich Text Format (RTF)	b4180d06-5931-4684-8ef4-7bf3588a5d9e
47	0	0	Y	2020-03-08 00:31:49.808131	0	2020-03-08 00:31:49.808131	0	.sh	application/x-sh	Bourne shell script	bbc6b9e0-6563-4a62-b846-1f4d2aca84c4
48	0	0	Y	2020-03-08 00:31:49.81059	0	2020-03-08 00:31:49.81059	0	.svg	image/svg+xml	Scalable Vector Graphics (SVG)	1b895baf-976e-4eee-93f9-64ca414af3bc
49	0	0	Y	2020-03-08 00:31:49.813209	0	2020-03-08 00:31:49.813209	0	.swf	application/x-shockwave-flash	Small web format (SWF) or Adobe Flash document	e4543169-5d34-489e-a1fc-080a71e93126
50	0	0	Y	2020-03-08 00:31:49.815228	0	2020-03-08 00:31:49.815228	0	.tar	application/x-tar	Tape Archive (TAR)	95a99fed-b176-4a46-a1dc-b62cd8953e77
51	0	0	Y	2020-03-08 00:31:49.81718	0	2020-03-08 00:31:49.81718	0	.tff	image/tiff	Tagged Image File Format (TIFF)	a79c585f-f34f-4092-8f54-852df613ba17
52	0	0	Y	2020-03-08 00:31:49.819477	0	2020-03-08 00:31:49.819477	0	.ts	video/mp2t	MPEG transport stream	ca0ce87a-98e9-4749-a87a-57ec754bb53d
53	0	0	Y	2020-03-08 00:31:49.821402	0	2020-03-08 00:31:49.821402	0	.ttf	font/ttf	TrueType Font	41cee1ef-7983-4d65-98cc-6ddea52277a3
54	0	0	Y	2020-03-08 00:31:49.823448	0	2020-03-08 00:31:49.823448	0	.txt	text/plain	Text, (generally ASCII or ISO 8859-n)	e09aae0f-1adc-4f80-91ea-39a60772821f
55	0	0	Y	2020-03-08 00:31:49.825395	0	2020-03-08 00:31:49.825395	0	.vsd	application/vnd.visio	Microsoft Visio	6ebce3a9-1bc6-4923-9beb-48a9928b0787
56	0	0	Y	2020-03-08 00:31:49.827282	0	2020-03-08 00:31:49.827282	0	.wav	audio/wav	Waveform Audio Format	2ea99d7b-8af1-43c9-a34d-b1723e1c3e8f
57	0	0	Y	2020-03-08 00:31:49.828995	0	2020-03-08 00:31:49.828995	0	.weba	audio/webm	WEBM audio	7131c8b4-2302-476d-b682-15a564c2639e
58	0	0	Y	2020-03-08 00:31:49.830661	0	2020-03-08 00:31:49.830661	0	.webm	video/webm	WEBM video	7df992f2-470b-47f8-9356-8dacaf3f84fc
59	0	0	Y	2020-03-08 00:31:49.832576	0	2020-03-08 00:31:49.832576	0	.webp	image/webp	WEBP image	587fc7bc-153c-4fda-a50d-5a082e551488
60	0	0	Y	2020-03-08 00:31:49.834645	0	2020-03-08 00:31:49.834645	0	.woff	font/woff	Web Open Font Format (WOFF)	559f6d28-567a-491e-8136-542642c983f1
61	0	0	Y	2020-03-08 00:31:49.836613	0	2020-03-08 00:31:49.836613	0	.woff2	font/woff2	Web Open Font Format (WOFF)	41a28e0f-d272-437b-ab83-a3ac066f6bd6
62	0	0	Y	2020-03-08 00:31:49.838604	0	2020-03-08 00:31:49.838604	0	.xhtml	application/xhtml+xml	XHTML	168c1caf-36f4-4220-a81b-371b25311a1a
63	0	0	Y	2020-03-08 00:31:49.841062	0	2020-03-08 00:31:49.841062	0	.xls	 application/vnd.ms-excel	Microsoft Excel	2eb45674-925b-42ce-ba9e-bca31d0360f8
64	0	0	Y	2020-03-08 00:31:49.843866	0	2020-03-08 00:31:49.843866	0	.xlsx	application/vnd.openxmlformats-officedocument.spreadsheetml.sheet	Microsoft Excel (OpenXML)	9f9327d8-b47f-40aa-ba57-e807674def14
65	0	0	Y	2020-03-08 00:31:49.847481	0	2020-03-08 00:31:49.847481	0	.xul	application/vnd.mozilla.xul+xml	XUL	8062c7ab-77a6-4d5a-b92e-3d88dfc095b3
66	0	0	Y	2020-03-08 00:31:49.849469	0	2020-03-08 00:31:49.849469	0	.zip	application/zip	ZIP archive	b0ebf216-0677-452c-9235-1caa6dfc30a4
67	0	0	Y	2020-03-08 00:31:49.851569	0	2020-03-08 00:31:49.851569	0	.3gp	video/3gpp audio/3gpp if it doesnt contain video	3GPP audio/video container	5a79a882-e2f2-430e-b24e-5982524b07a5
68	0	0	Y	2020-03-08 00:31:49.85364	0	2020-03-08 00:31:49.85364	0	.3g2	video/3gpp2 audio/3gpp2 if it doesnt contain video	3GPP2 audio/video container	86032d5c-c9ec-47d0-9578-6b2eb5667aed
69	0	0	Y	2020-03-08 00:31:49.855605	0	2020-03-08 00:31:49.855605	0	.7z	application/x-7z-compressed	7-zip archive	8906a796-2280-489d-bc7f-44d4f793379c
\.


--
-- Data for Name: ad_message; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_message (ad_message_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, value, msgtext, msgtip, msgtype, ad_message_uu) FROM stdin;
1	0	0	Y	2020-02-26 19:21:37.710365	0	2020-02-26 19:21:37.710365	0	NoEntityFound	No entity found for query	\N	E	eaf28f83-0719-4861-a1ce-7beff98daa83
2	0	0	Y	2020-12-28 13:24:45.133711	0	2020-12-28 13:24:45.133711	0	InvalidFormatFile	Invalid file format	\N	E	\N
3	0	0	Y	2020-12-28 13:25:10.143235	0	2020-12-28 13:25:10.143235	0	BadRequest	Invalid request	\N	E	\N
4	0	0	Y	2020-12-28 13:25:29.200866	0	2020-12-28 13:25:29.200866	0	InvalidIdentityProviderConfiguration	Configuration is invalid for the provider	\N	E	\N
5	0	0	Y	2020-12-28 13:25:53.13052	0	2020-12-28 13:25:53.13052	0	InvalidValue	Invalid value	\N	E	\N
6	0	0	Y	2020-12-28 13:26:14.919732	0	2020-12-28 13:26:14.919732	0	ErrorSendEmail	Error in sending email	\N	E	\N
7	0	0	Y	2020-12-28 13:26:36.530002	0	2020-12-28 13:26:36.530002	0	DBNonUniqueResultException	More than one row with the given identifier was found	\N	E	\N
8	0	0	Y	2020-12-28 13:26:58.908232	0	2020-12-28 13:26:58.908232	0	DBNoConnection	No database connection	\N	E	\N
9	0	0	Y	2020-12-28 13:27:56.603111	0	2020-12-28 13:27:56.603111	0	ErrorCreateRecord	Record creation error	\N	E	\N
10	0	0	Y	2020-12-28 13:28:35.910756	0	2020-12-28 13:28:35.910756	0	ErrorDeleteRecord	Record delete error	\N	E	\N
11	0	0	Y	2020-12-28 13:28:59.015211	0	2020-12-28 13:28:59.015211	0	NotSupported	Not supported	\N	E	\N
12	0	0	Y	2020-12-28 13:29:19.106463	0	2020-12-28 13:29:19.106463	0	InvalidProcess	Invalid process identifier	\N	E	\N
13	0	0	Y	2020-12-28 13:29:40.66833	0	2020-12-28 13:29:40.66833	0	ServiceNotSupported	Service not supported	\N	E	\N
14	0	0	Y	2020-12-28 13:30:02.683188	0	2020-12-28 13:30:02.683188	0	ServiceNotImplemented	Service not implemented	\N	E	\N
15	0	0	Y	2020-12-28 13:30:23.902481	0	2020-12-28 13:30:23.902481	0	AccountNotVerified	Account not verified	\N	E	\N
16	0	0	Y	2020-12-28 13:30:45.578396	0	2020-12-28 13:30:45.578396	0	EmailAlreadyExists	Email already exists	\N	E	\N
17	0	0	Y	2020-12-28 13:31:08.083344	0	2020-12-28 13:31:08.083344	0	InvalidParams	Invalid parameters	\N	E	\N
18	0	0	Y	2020-12-28 13:31:27.554965	0	2020-12-28 13:31:27.554965	0	FBErrorGettingData	Error getting data from Facebook	\N	E	\N
19	0	0	Y	2020-12-28 13:31:47.20016	0	2020-12-28 13:31:47.20016	0	GoogleErrorGettingData	Error getting data from Google	\N	E	\N
20	0	0	Y	2020-12-28 13:32:23.683415	0	2020-12-28 13:32:23.683415	0	NotAllowed	Not allowed	\N	E	\N
\.


--
-- Data for Name: ad_message_trl; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_message_trl (ad_message_id, ad_client_id, ad_org_id, ad_language, isactive, created, createdby, updated, updatedby, msgtext, msgtip) FROM stdin;
1	0	0	pt_BR	Y	2020-02-26 19:39:16.389867	0	2020-02-26 19:39:16.389867	0	Nenhum registro encontrado	\N
2	0	0	pt_BR	Y	2020-12-28 13:24:56.788255	0	2020-12-28 13:24:56.788255	0	Formato de arquivo inválido	\N
3	0	0	pt_BR	Y	2020-12-28 13:25:16.898035	0	2020-12-28 13:25:16.898035	0	Requisição inválida	\N
4	0	0	pt_BR	Y	2020-12-28 13:25:37.304023	0	2020-12-28 13:25:37.304023	0	Configurações inválidas para o provedor	\N
5	0	0	pt_BR	Y	2020-12-28 13:26:03.807171	0	2020-12-28 13:26:03.807171	0	Valor inválido	\N
6	0	0	pt_BR	Y	2020-12-28 13:26:21.61869	0	2020-12-28 13:26:21.61869	0	Erro ao enviar e-mail	\N
7	0	0	pt_BR	Y	2020-12-28 13:26:46.129694	0	2020-12-28 13:26:46.129694	0	Foi encontrada mais do que uma linha com o identificador indicado	\N
8	0	0	pt_BR	Y	2020-12-28 13:27:25.920819	0	2020-12-28 13:27:25.920819	0	Sem conexão com o banco de dados	\N
9	0	0	pt_BR	Y	2020-12-28 13:28:19.776814	0	2020-12-28 13:28:19.776814	0	Erro ao criar registro	\N
10	0	0	pt_BR	Y	2020-12-28 13:28:46.206357	0	2020-12-28 13:28:46.206357	0	Erro ao excluir registro	\N
11	0	0	pt_BR	Y	2020-12-28 13:29:06.490563	0	2020-12-28 13:29:06.490563	0	Não suportado	\N
12	0	0	pt_BR	Y	2020-12-28 13:29:27.837868	0	2020-12-28 13:29:27.837868	0	Identificador do processo inválido	\N
13	0	0	pt_BR	Y	2020-12-28 13:29:48.751229	0	2020-12-28 13:29:48.751229	0	Serviço não suportado	\N
14	0	0	pt_BR	Y	2020-12-28 13:30:10.320144	0	2020-12-28 13:30:10.320144	0	Serviço não implementado	\N
15	0	0	pt_BR	Y	2020-12-28 13:30:31.37534	0	2020-12-28 13:30:31.37534	0	Conta não verificada	\N
16	0	0	pt_BR	Y	2020-12-28 13:30:53.58295	0	2020-12-28 13:30:53.58295	0	Email já existente	\N
17	0	0	pt_BR	Y	2020-12-28 13:31:14.625327	0	2020-12-28 13:31:14.625327	0	Parâmetros inválidos	\N
18	0	0	pt_BR	Y	2020-12-28 13:31:33.976024	0	2020-12-28 13:31:33.976024	0	Erro ao obter dados do Facebook	\N
19	0	0	pt_BR	Y	2020-12-28 13:32:08.529931	0	2020-12-28 13:32:08.529931	0	Erro ao obter dados do Google	\N
20	0	0	pt_BR	Y	2020-12-28 13:32:30.931671	0	2020-12-28 13:32:30.931671	0	Não permitido	\N
\.


--
-- Data for Name: ad_modelvalidator; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_modelvalidator (ad_client_id, ad_modelvalidator_id, ad_org_id, created, createdby, updated, updatedby, isactive, name, description, help, modelvalidationclass, seqno, ad_modelvalidator_uu, ad_table_id, ad_extension_id) FROM stdin;
0	1	0	2020-02-06 02:25:56.044782	0	2020-02-06	0	Y	User Validator	\N	\N	com.cadre.server.core.validator.ValidatorUser	10	\N	12	0
\.

--
-- Data for Name: ad_notificationtemplate_trl; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_notificationtemplate_trl (ad_notificationtemplate_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, description, header, template, ad_language) FROM stdin;
\.


--
-- Data for Name: ad_oauth2_client; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_oauth2_client (ad_oauth2_client_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, clientid, clientsecret, islocked, dateaccountlocked, ad_oauth2_client_uu, isadmin, tokenexpiresin, ad_user_id, isrefreshtokenexpires, refreshtokenvalidity, ad_app_id) FROM stdin;
2	0	0	Y	2020-01-24 17:13:34.663609	0	2020-10-29 17:14:52.042	0	ng-cadre-webapp	Cadre Frontend	cadre.api.oauth2-client.ng-cadre-webapp.4feb6a7db5c6621b0b07adc8eb8b64e9	b9d9f7ab5c06430c176365ba27327bbc	N	\N	\N	Y	3600	0	Y	0	1
\.


--
-- Data for Name: ad_oauth2_client_token; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_oauth2_client_token (ad_oauth2_client_token_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, accesstoken, accesstokenexpiration, authorizationcode, authorizationcodeexpiration, isactiveaccesstoken, refreshtoken, refreshtokenexpiration, ad_user_id, ad_oauth2_client_id, ad_oauth2_client_token_uu, isactiverefreshtoken, ad_app_id) FROM stdin;
\.


--
-- Data for Name: ad_oauth_client_roles; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_oauth_client_roles (ad_oauth2_client_id, ad_role_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_oauth_client_roles_uu, ad_oauth_client_roles_id) FROM stdin;
2	2	0	0	Y	2020-02-12 00:57:00.595016	0	2020-02-12 00:57:00.595016	0	a44b752a-4cf6-4d6a-beaa-a9a561ec8f3b	1
\.


--
-- Data for Name: ad_object_access; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_object_access (value, ad_resource_type_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_role_id, ad_object_access_uu, isreadonly, ad_object_access_id, isexactlymatch) FROM stdin;
\.


--
-- Data for Name: ad_org; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_org (ad_org_id, ad_client_id, isactive, created, createdby, updated, updatedby, value, name, description, ad_org_uu) FROM stdin;
0	0	Y	2020-01-11 15:05:26.405804	0	2020-01-11 15:05:26.405804	0	All Organizations	*	\N	fd7eae1d-ca8d-4e19-a083-f82b58dc390f
\.


--
-- Data for Name: ad_process; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_process (ad_process_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, value, description, help, procedurename, ad_extension_id, ad_scripting_id, ad_process_uu) FROM stdin;
1	0	0	Y	2021-05-06 21:20:01.739453	0	2021-05-06 21:20:01.739453	0	syncTableDatabase	\N	\N	syncTableDatabase	0	\N	2b6bd92b-05b0-4420-8089-e683ebb38b07
\.


--
-- Data for Name: ad_process_para; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_process_para (ad_client_id, ad_process_para_id, ad_process_para_uu, ad_org_id, created, createdby, defaultvalue, description, help, isactive, ismandatory, issameline, label, placeholder, seqno, updated, updatedby, ad_process_id, bootstrapclass, ad_extension_id, dynamicvalidation, columnname, ad_reference_id, ad_reference_value_id) FROM stdin;
0	4	21cca889-1b8d-48ff-b694-22a3e3b58416	0	2021-05-08 21:22:24.951037	0	\N	\N	\N	Y	Y	Y	Table name	\N	10	2021-05-08 21:23:27.249	0	1	col-md-6 mb-3	0	\N	tableName	10	\N
0	6	e3f11383-b662-4a53-b481-1d77a3c5056e	0	2021-05-08 21:24:12.172151	0	\N	\N	\N	Y	Y	Y	Extension	\N	30	2021-05-08 22:01:16.374	0	1	col-md-6 mb-3	0	\N	adExtensionId	18	50
0	5	b430faa4-3420-4810-a544-426e2baea070	0	2021-05-08 21:23:53.157272	0	\N	\N	\N	Y	Y	Y	Sync table from database	\N	20	2021-05-08 22:08:16.287	0	1	col-md-6 mb-3	0	\N	syncFromDatabase	20	\N
\.


--
-- Data for Name: ad_ref_list; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_ref_list (ad_ref_list_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, value, name, description, ad_reference_id, ad_extension_id, ad_ref_list_uu) FROM stdin;
1	0	0	Y	2020-03-27 10:32:08.149312	0	2020-03-27 10:32:08.149312	0	D	DataType	\N	1	0	\N
2	0	0	Y	2020-03-27 10:33:32.201009	0	2020-03-27 10:33:32.201009	0	L	List Validation	\N	1	0	\N
3	0	0	Y	2020-03-27 10:33:59.478817	0	2020-03-27 10:33:59.478817	0	T	Table Validation	\N	1	0	\N
4	0	0	Y	2020-03-27 13:54:25.324565	0	2020-03-27 13:54:25.324565	0	E	Error	\N	41	0	\N
6	0	0	Y	2020-03-27 13:54:39.35835	0	2020-03-27 13:54:39.35835	0	I	Information	\N	41	0	\N
7	0	0	Y	2020-03-27 13:55:03.754415	0	2020-03-27 13:55:03.754415	0	W	Warning	\N	41	0	\N
9	0	0	Y	2020-04-15 22:41:56.06418	0	2020-04-15 22:41:56.06418	0	2	Notification Provider	\N	43	0	\N
13	0	0	Y	2020-04-19 16:35:09.649838	0	2020-04-19 16:35:09.649838	0	1	Organization	\N	45	0	b0dc1162-184e-405f-8923-a1bea9f030e3
14	0	0	Y	2020-04-19 16:35:19.05361	0	2020-04-19 16:35:19.05361	0	2	Client Only	\N	45	0	d3a48b1c-991c-4d64-b48a-82b6a6b7e853
15	0	0	Y	2020-04-19 16:35:31.374061	0	2020-04-19 16:35:31.374061	0	3	Client+Organization	\N	45	0	877080fb-9dac-4971-9dd0-f0dfa68240dc
16	0	0	Y	2020-04-19 16:35:43.490323	0	2020-04-19 16:35:43.490323	0	4	System only	\N	45	0	7367226e-9b21-4ab4-94bf-f2ecff41c289
17	0	0	Y	2020-04-19 16:36:00.438648	0	2020-04-19 16:36:00.438648	0	6	System+Client	\N	45	0	e5d8fc68-6749-4822-80d9-50bac4dbae73
18	0	0	Y	2020-04-19 16:36:12.052552	0	2020-04-19 16:36:12.052552	0	7	All	\N	45	0	1ae5c263-1044-4694-8781-e005e52e8dc3
19	0	0	Y	2020-04-19 16:40:30.471684	0	2020-04-19 16:40:30.471684	0	 C 	Client	\N	46	0	65cdc744-37db-494a-9d40-594bd2840efa
20	0	0	Y	2020-04-19 16:40:44.87727	0	2020-04-19 16:40:44.87727	0	 CO	Client+Organization	\N	46	0	faff54c9-eccb-40e0-800c-eb75fc0abf64
21	0	0	Y	2020-04-19 16:41:02.700471	0	2020-04-19 16:41:02.700471	0	  O	Organization	\N	46	0	fb3258d7-1497-4e98-83e7-37b0d4e9a804
22	0	0	Y	2020-04-19 16:41:24.441253	0	2020-04-19 16:41:24.441253	0	S  	System	\N	46	0	ebd99e66-b8b2-4e51-a92c-da8e09df54e6
23	0	0	Y	2020-05-05 09:51:05.696593	0	2020-05-05 09:51:05.696593	0	4	Custom Service Impl	Service is an implementation for an interface	43	0	e90d61da-1769-49bc-96d3-c1ed99f6f546
24	0	0	Y	2020-05-05 09:55:32.803555	0	2020-05-05 09:55:32.803555	0	5	EventHandler	EventHandler objects can inspect the received Event object to determine its topic and properties.	43	0	dc6e9ec0-4427-4bd0-9166-5d204d0486d1
10	0	0	Y	2020-04-15 23:51:58.648691	0	2020-04-15 23:51:58.648691	0	3	Storage	Ex: aws-s3, dropbox, google drive	43	0	\N
8	0	0	Y	2020-04-15 22:41:26.513558	0	2020-04-15 22:41:26.513558	0	1	Identity Provider	Login Modules	43	0	\N
26	0	0	Y	2020-10-29 09:56:44.412199	0	2020-10-29 13:58:07.952	0	3	Static Value	\N	47	0	031a1ecc-c677-4356-bd3c-03bbe930d941
25	0	0	Y	2020-10-29 09:56:33.630469	0	2020-10-29 13:58:10.786	0	2	Classname	\N	47	0	ce37ca2e-1e9a-402a-a14b-4d99901f2a3b
27	0	0	Y	2020-10-29 09:56:53.154326	0	2020-10-29 13:58:13.71	0	1	SQL	\N	47	0	bc41c6cd-2023-4336-8e77-cd6451ab5708
28	0	0	Y	2021-04-28 11:52:58.295207	0	2021-04-28 11:52:58.295207	0	A	 Aspect Orient Program	\N	48	0	7a996c09-32f8-4452-a828-afbc901130a9
29	0	0	Y	2021-04-28 11:53:14.594466	0	2021-04-28 11:53:14.594466	0	S	JSR 223 Scripting APIs	\N	48	0	e47ffec4-43b0-4c5b-bfac-11a681502411
30	0	0	Y	2021-04-28 11:55:10.005613	0	2021-04-28 11:55:10.005613	0	R	JSR 94 Engine Type API	\N	48	0	f0e3805f-1ad3-481c-8398-938a45e49564
\.


--
-- Data for Name: ad_reference; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_reference (ad_reference_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, help, validationtype, isorderbyvalue, ad_reference_uu, ad_extension_id, ad_table_id) FROM stdin;
10	0	0	Y	2019-12-30 21:50:04.269099	0	2019-12-30 21:50:04.269099	100	String	Character String		D	N	3f9a0323-219e-4b97-af37-58a2486a50e3	0	\N
11	0	0	Y	2019-12-30 21:50:04.281171	0	2019-12-30 21:50:04.281171	100	Integer	10 Digit numeric		D	N	04246702-9e19-4942-89d8-643ca98535ef	0	\N
12	0	0	Y	2019-12-30 21:50:04.284555	0	2019-12-30 21:50:04.284555	100	Amount	Number with 4 decimals		D	N	75558709-04db-4ea0-8a21-ee4dc1dff574	0	\N
13	0	0	Y	2019-12-30 21:50:04.287271	0	2019-12-30 21:50:04.287271	100	ID	10 Digit Identifier		D	N	caf89cca-530b-42c2-9b19-1749b16679de	0	\N
14	0	0	Y	2019-12-30 21:50:04.289953	0	2019-12-30 21:50:04.289953	100	Text	Character String up to 2000 characters		D	N	a52011ae-3d88-4a9b-8179-a42330a26f5b	0	\N
15	0	0	Y	2019-12-30 21:50:04.292617	0	2019-12-30 21:50:04.292617	100	Date	Date mm/dd/yyyy		D	N	fea92a20-5ea7-445c-b010-101d74f5e124	0	\N
16	0	0	Y	2019-12-30 21:50:04.295518	0	2019-12-30 21:50:04.295518	100	Date+Time	Date with time		D	N	9e410fd2-9e6c-4b19-842a-ef4634405244	0	\N
17	0	0	Y	2019-12-30 21:50:04.298571	0	2019-12-30 21:50:04.298571	100	List	Reference List		D	N	70bc89e7-5848-4ff5-a1f0-e1b1aed3796f	0	\N
18	0	0	Y	2019-12-30 21:50:04.302585	0	2019-12-30 21:50:04.302585	100	Table	Table List		D	N	d79e151d-6401-4e9f-8549-15357111a1ed	0	\N
19	0	0	Y	2019-12-30 21:50:04.305712	0	2019-12-30 21:50:04.305712	100	Table Direct	Direct Table Access		D	N	85767149-7019-4224-a8d0-41f0d9d77d47	0	\N
20	0	0	Y	2019-12-30 21:50:04.308504	0	2019-12-30 21:50:04.308504	100	Yes-No	CheckBox		D	N	1dab6dbd-8d84-4ba5-87df-e2ca93d2c976	0	\N
24	0	0	Y	2019-12-30 21:50:04.311172	0	2019-12-30 21:50:04.311172	0	Time	Time		D	N	7450ca2c-8ddd-4f10-988d-7ae5b9a3f7f3	0	\N
32	0	0	Y	2019-12-30 21:50:04.317139	0	2019-12-30 21:50:04.317139	0	Image	Binary Image Data		D	N	23efa169-7f2d-4557-8f24-12db6442e985	0	\N
43	0	0	Y	2020-04-15 22:40:57.948997	0	2020-04-15 22:40:57.948997	0	Service Type	Service Type	\N	L	Y	\N	0	\N
44	0	0	Y	2020-04-16 09:48:37.516234	0	2020-04-16 09:48:37.516234	0	AD_TreeNode	\N	\N	T	N	\N	0	15
45	0	0	N	2020-04-19 16:33:55.243663	0	2020-04-19 16:33:55.243663	0	AD_Table Access Levels	Table Access and Sharing Level list	\N	L	N	9e7c8533-b90c-47ac-9547-798060054b99	0	\N
46	0	0	N	2020-04-19 16:40:11.404262	0	2020-04-19 16:40:11.404262	0	AD_Role User Level	\N	\N	L	N	999634fd-281b-4883-9d8b-076659b4ef6c	0	\N
28	0	0	Y	2019-12-30 21:50:04.314144	0	2019-12-30 21:50:04.314144	0	Collection	Collection of values separated by semicolons		D	N	27b0ba03-0c18-4657-8981-00d12909751a	0	\N
1	0	0	Y	2020-03-27 10:12:36.364987	0	2020-03-27 10:12:36.364987	0	AD_Reference Validation Types	Reference Validation Type list	e.g. I - Independent 	T	N	\N	0	6
36	0	0	Y	2019-12-30 21:50:04.320682	0	2019-12-30 21:50:04.320682	0	Map Values	An object that maps keys to values	A map cannot contain duplicate keys; each key can map to at most one value.	D	N	678761e9-b461-4afe-a343-6216de60c6c4	0	\N
47	0	0	Y	2020-10-29 09:56:08.734942	0	2020-10-29 09:56:08.734942	0	AD_Variable_Type	\N	\N	L	Y	08909c51-cab3-4c30-a23c-4aee1a7086b6	0	\N
40	0	0	Y	2020-03-27 13:43:28.485766	0	2020-03-27 13:43:28.485766	0	AD_Column	\N	\N	T	N	\N	0	5
2	0	0	Y	2020-03-27 10:46:06.597919	0	2020-03-27 10:46:06.597919	0	AD_Reference		\N	T	N	\N	0	6
41	0	0	Y	2020-03-27 13:53:06.470067	0	2020-03-27 13:53:06.470067	0	AD_Message Type	Message Type list	\N	L	N	\N	0	\N
42	0	0	N	2020-03-27 19:28:03.449005	0	2020-03-27 19:28:03.449005	0	Password	\N	\N	D	N	\N	0	\N
48	0	0	Y	2021-04-28 11:52:19.78876	0	2021-04-28 11:52:19.78876	0	AD_Scripting_EngineType	\N	\N	L	Y	3247911f-d2a7-4b9d-823e-abce50e03963	0	\N
49	0	0	Y	2021-05-06 21:35:05.238571	0	2021-05-06 21:35:05.238571	0	AD_User_Table	AD_User_Table	\N	T	N	15d60f2f-6731-42d4-934d-9d110bdcc762	0	12
50	0	0	Y	2021-05-08 22:00:04.33969	0	2021-05-08 22:01:02.339	0	AD_Extension	\N	\N	T	N	f225824a-f381-41fc-8ea3-f06bf3744216	0	26
51	0	0	N	2021-05-11 18:00:12.196096	0	2021-05-11 18:00:12.196096	0	File	\N	\N	D	N	5de4aefb-86ae-40e0-a89e-77cbfccb137d	0	\N
\.


--
-- Data for Name: ad_resource_type; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_resource_type (ad_resource_type_id, ad_client_id, ad_org_id, isactive, created, createdby, name, updated, updatedby, ad_resource_type_uu, ad_extension_id) FROM stdin;
1	0	0	Y	2020-03-12 11:01:53.417267	0	Table and Column	2020-03-12 11:01:53.417267	0	849eb757-843b-4245-ab06-df14598cc05b	0
2	0	0	Y	2020-03-12 11:01:53.433447	0	Process	2020-03-12 11:01:53.433447	0	667bd5e9-f4b8-4168-bc2e-7d68585c5a78	0
3	0	0	Y	2020-03-12 11:01:54.30138	0	Report	2020-03-12 11:01:54.30138	0	7fb34ecf-ab45-491c-ba7a-21578b270e6b	0
4	0	0	Y	2020-04-01 23:06:45.980903	0	EndPoint	2020-04-01 23:06:45.980903	0	d88e19c7-3b6d-4341-ba0a-740fd8aaec02	0
\.


--
-- Data for Name: ad_role; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_role (ad_role_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, name, updatedby, description, ad_role_uu, userlevel) FROM stdin;
2	0	0	Y	2020-02-12 00:52:51.910798	0	2020-10-26 20:26:56.02	OAuth Login 2	0	OAuth2 Login	05ea765e-c66c-4a46-a337-e859a2475acc	S
\.


--
-- Data for Name: ad_scripting; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_scripting (ad_client_id, ad_org_id, ad_scripting_id, createdby, description, isactive, name, enginetype, content, updatedby, value, ad_scripting_uu, created, updated) FROM stdin;
0	0	7	0	\N	Y	teste	S	\t\t\t\t\t\timport com.cadre.server.core.broker.NotificationBroker;\n\t\t\t\t\t\timport javax.enterprise.inject.spi.CDI;\n \n\n\t\t\t\t\t\tNotificationBroker notificationService = CDI.current().select(NotificationBroker.class).get();\n\t\t\t\t\t\tnotificationService.sendNotificationlWithTemplate(NotificationBroker.NOTIFICATION_TYPE_EMAIL,\n\t\t\t\t\t\t\t\t"m87.fernando@gmail.com", // to\n\t\t\t\t\t\t\t\t"lancamento", null// values\n\t\t\t\t\t\t);	0	teste	\N	2021-04-28 12:24:47.39953	2021-04-28 15:30:58.393
\.


--
-- Data for Name: ad_serviceprovider; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_serviceprovider (ad_serviceprovider_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, value, attributes, ad_serviceprovider_uu, ad_extension_id, classname, servicetype) FROM stdin;
1	0	0	Y	2020-04-10 13:15:11.096857	0	2020-04-10 13:15:11.096857	0	default	\N	17848b2d-07fd-43a4-9f76-86a8c73c871e	0	com.cadre.server.core.security.impl.UsernamePasswordLoginRealm	1
6	0	0	Y	2020-04-10 13:09:00.755498	0	2020-04-10 13:09:00.755498	0	facebook	appSecret:45a5d95264235b2fd3215f63d743138a	\N	0	com.cadre.server.ext.facebook.FacebookProvider	1
7	0	0	Y	2020-04-11 22:02:13.44061	0	2020-04-11 22:02:13.44061	0	google	clientId:140618949962-bhq02geskeo3rfg6fum0aemjpk6j46ab.apps.googleusercontent.com	\N	0	com.cadre.server.ext.google.GoogleProvider	1
8	0	0	Y	2020-04-15 23:52:33.010761	0	2020-04-15 23:52:33.010761	0	aws-s3	\N	\N	0	com.cadre.server.ext.amazon.s3.AWSS3StoreImpl	3
9	0	0	Y	2020-04-15 23:53:22.655589	0	2020-04-15 23:53:22.655589	0	empty	\N	\N	0	com.cadre.server.core.attachment.EmptyStoreImpl	3
10	0	0	Y	2020-04-16 11:06:09.82964	0	2020-04-16 11:06:09.82964	0	email	\N	\N	0	com.cadre.server.core.notification.SendEmailSMTP	2
11	0	0	Y	2020-05-05 09:51:55.917928	0	2020-05-05 09:51:55.917928	0	defaultDataAccessImpl	\N	10ad43b6-ca29-4283-b362-136a4447d0ae	0	com.cadre.server.core.service.impl.DataAccessServiceImpl	4
13	0	0	Y	2020-05-05 09:52:43.232337	0	2020-05-05 09:52:43.232337	0	defaultOAuth2ServiceImpl	\N	3423cd0a-8992-40ab-9eeb-611c791d021a	0	com.cadre.server.core.service.impl.OAuth2ServiceImpl	4
\.


--
-- Data for Name: ad_sysconfig; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_sysconfig (ad_sysconfig_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, value, name, description, ad_sysconfig_uu) FROM stdin;
1	0	0	Y	2020-09-14 20:26:51.112479	0	2020-09-14 20:26:51.112479	0	MyPersonalKey@10283012831	SECRET_KEY	\N	3b753907-f53a-43ac-ae63-792b5042d25d
2	0	0	Y	2020-09-14 20:27:18.680531	0	2020-09-14 20:27:18.680531	0	http://localhost:4201	HTTP_BASE_HOST	\N	bd31f97f-1538-4923-9ef2-6d56262c3a90
\.


--
-- Data for Name: ad_tab; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_tab (ad_tab_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, help, ad_table_id, ad_window_id, seqno, tablevel, isreadonly, isinsertrecord, parent_column_id, ad_tab_uu, ad_extension_id, orderbyclause) FROM stdin;
1	0	0	Y	2020-01-22 20:43:48.079407	0	2020-01-22 20:43:48.079407	0	Resource Type			1	1	1	0	N	Y	\N	\N	0	\N
18	0	0	Y	2020-03-27 17:50:04.473516	0	2020-03-27 17:50:04.473516	0	Language			32	8	10	0	N	Y	\N	\N	0	AD_Language
3	0	0	Y	2019-12-30 22:04:07.090352	0	2019-12-30 22:04:07.090352	0	Column	Table Column definitions	Defines the columns of a table. Note thet the name of the column is automatically syncronized.	5	2	20	1	N	Y	50	e74a4cc2-62aa-44df-bafb-5fecef0078ad	0	ColumnName
5	0	0	Y	2019-12-30 22:04:07.090352	0	2019-12-30 22:04:07.090352	0	Tab	Tab definition within a window holds fields	The Tab Tab defines each Tab within a Window.  Each Tab contains a discrete selection of fields. Note that the display and read only logic is evaluated when loading the window.	8	4	20	1	N	Y	66	85552e00-a1a3-48c4-95c4-f921abcf737f	0	SeqNo
4	0	0	Y	2019-12-30 22:04:07.090352	0	2019-12-30 22:04:07.090352	0	Windows	Window header definitions	The Window Tab defines each window in the system. The default flag indicates that this window should be used as the default Zoom windows for the tables in this window.	7	4	10	0	N	Y	\N	b7bdec10-0ffc-4b08-9a14-fb62af349165	0	Name
6	0	0	Y	2019-12-30 22:04:07.090352	0	2019-12-30 22:04:07.090352	0	Field	Field definitions in tabs in windows	The Field Tab defines the Fields displayed within a tab.  Changes made to the Field Tab become visible after restart due to caching. If the Sequence is negative, the record are ordered descending. Note that the name, description and help is automatically synchronized if centrally maintained.	9	4	30	2	N	Y	72	f0b7f75c-f654-49ea-9e61-8814d820cdd7	0	SeqNo
11	0	0	Y	2020-03-25 21:31:33.549844	0	2020-03-25 21:31:33.549844	0	Menu			14	9	10	0	N	Y	\N	\N	0	\N
32	0	0	Y	2020-04-16 09:04:25.656343	0	2020-04-16 09:04:25.656343	0	Template	\N	\N	40	18	10	0	N	Y	\N	\N	0	Name
2	0	0	Y	2019-12-30 22:04:07.090352	0	2019-12-30 22:04:07.090352	0	Table	Table definitions	Table (header) definition - Note that the name of most tables is automatically syncronized.	4	2	10	0	N	Y	\N	8416a3a3-725e-41a4-a434-cff771315de5	0	TableName
33	0	0	Y	2020-04-16 09:26:44.846832	0	2020-04-16 09:26:44.846832	0	Mail Config	\N	\N	41	19	10	0	N	Y	\N	\N	0	\N
13	0	0	Y	2020-03-27 09:56:37.350462	0	2020-03-27 09:56:37.350462	0	Reference			6	3	10	0	N	Y	\N	\N	0	Name
14	0	0	Y	2020-03-27 09:57:01.486851	0	2020-03-27 09:57:01.486851	0	List Validation			33	3	20	1	N	Y	406	\N	0	\N
7	0	0	Y	2019-12-30 22:04:07.090352	0	2019-12-30 22:04:07.090352	0	Message	Information Error and Menu Messages	The Message Tab displays error message text and menu messages	10	5	10	0	N	Y	\N	c80b4923-ddda-4665-a728-8c249cda2bdc	0	Value
15	0	0	Y	2020-03-27 17:24:15.105181	0	2020-03-27 17:24:15.105181	0	Model Validator			24	2	30	1	N	Y	272	\N	0	SeqNo
16	0	0	Y	2020-03-27 17:33:12.499295	0	2020-03-27 17:33:12.499295	0	Client			11	7	10	0	N	Y	\N	\N	0	Value
17	0	0	Y	2020-03-27 17:43:12.083037	0	2020-03-27 17:43:12.083037	0	Organization			13	7	20	1	N	Y	105	\N	0	Name
20	0	0	Y	2020-03-27 18:12:19.947793	0	2020-03-27 18:12:19.947793	0	Extension			26	11	10	0	N	Y	\N	\N	0	SeqNo
21	0	0	Y	2020-03-27 18:24:12.763978	0	2020-03-27 18:24:12.763978	0	User			12	15	10	0	N	Y	\N	\N	0	EMailUser
22	0	0	Y	2020-03-27 18:54:28.740357	0	2020-03-27 18:54:28.740357	0	User Roles			34	15	20	1	N	Y	413	\N	0	\N
23	0	0	Y	2020-03-27 19:07:35.74212	0	2020-03-27 19:07:35.74212	0	Role			35	16	10	0	N	Y	\N	\N	0	\N
34	0	0	Y	2020-06-15 17:54:21.912569	0	2020-06-15 17:54:21.912569	0	User App(s)	\N	\N	43	15	30	1	N	Y	551	\N	0	\N
43	0	0	Y	2021-02-06 15:32:10.066203	0	2021-02-06 15:32:10.066203	0	App	\N	\N	52	17	10	0	N	Y	\N	\N	0	\N
25	0	0	Y	2020-03-27 19:14:11.459001	0	2020-03-27 19:14:11.459001	0	Resource Access			21	16	20	1	N	Y	239	\N	0	\N
46	0	0	Y	2021-05-06 21:05:09.731182	0	2021-05-06 21:05:09.731182	0	Process	\N	\N	54	24	10	0	N	Y	\N	\N	0	\N
28	0	0	Y	2020-03-27 19:59:36.681729	0	2020-03-27 19:59:36.681729	0	OAuth Client Assingnment			36	16	40	1	N	Y	439	\N	0	\N
29	0	0	Y	2020-03-27 20:06:43.684272	0	2020-03-27 20:06:43.684272	0	Media Format			29	14	10	0	N	Y	\N	\N	0	\N
30	0	0	Y	2020-03-27 20:10:55.842064	0	2020-03-27 20:10:55.842064	0	Folder			30	13	10	0	N	Y	\N	\N	0	\N
24	0	0	Y	2020-03-27 19:11:03.669655	0	2020-03-27 19:11:03.669655	0	User Assingnment			34	16	35	1	N	Y	414	\N	0	\N
35	0	0	Y	2020-06-16 19:58:40.778099	0	2020-06-16 19:58:40.778099	0	Window(s) Access	\N	\N	44	16	30	1	N	Y	570	\N	0	\N
36	0	0	Y	2020-09-14 20:12:54.811252	0	2020-09-14 20:12:54.811252	0	Config	\N	\N	45	20	10	0	N	Y	\N	\N	0	Name
19	0	0	Y	2020-03-27 17:59:23.825732	0	2020-03-27 17:59:23.825732	0	Service Provider			27	10	10	0	N	Y	\N	\N	0	Value
12	0	0	Y	2020-03-25 21:33:46.441551	0	2020-03-25 21:33:46.441551	0	Tree Node			15	9	20	1	N	Y	180	\N	0	AD_TreeNode_Parent_ID
38	0	0	Y	2020-10-29 10:11:36.844327	0	2020-10-29 10:11:36.844327	0	Variable	\N	\N	47	21	10	0	N	Y	\N	\N	0	Value
39	0	0	Y	2020-12-15 17:49:37.635813	0	2020-12-15 17:49:37.635813	0	Job Definition	\N	\N	48	22	10	0	N	Y	\N	\N	0	\N
40	0	0	Y	2020-12-15 17:53:54.083678	0	2020-12-15 18:54:07.256	0	Scheduler	\N	\N	49	22	20	1	N	Y	642	\N	0	\N
41	0	0	Y	2020-12-28 13:11:34.859199	0	2020-12-28 14:11:44.253	0	Translate	\N	\N	50	5	20	1	N	Y	645	\N	0	AD_Language
42	0	0	Y	2021-01-01 13:04:27.161397	0	2021-01-01 14:04:33.761	0	Translation	\N	\N	51	18	20	1	N	Y	656	\N	0	AD_Language
47	0	0	Y	2021-05-08 20:13:32.301065	0	2021-05-08 21:00:39.98	0	Parameter	\N	\N	56	24	20	1	N	Y	747	\N	0	\N
27	0	0	Y	2020-03-27 19:54:09.910063	0	2021-02-06 16:32:32.705	0	Role			36	17	30	1	N	Y	438	\N	0	\N
37	0	0	Y	2020-10-29 10:03:47.528748	0	2021-02-06 16:32:38.009	0	Rules	\N	\N	46	17	40	1	N	Y	595	\N	0	\N
26	0	0	Y	2020-03-27 19:29:04.350475	0	2021-02-06 16:33:27.918	0	OAuth Client			2	17	20	1	N	Y	682	\N	0	\N
44	0	0	Y	2021-04-28 11:47:00.088717	0	2021-04-28 11:47:00.088717	0	Scripting	\N	\N	53	23	10	0	N	Y	\N	\N	0	\N
31	0	0	Y	2020-03-27 20:19:51.093286	0	2021-05-04 08:13:11.576	0	Media			28	13	20	1	N	Y	322	\N	0	
45	0	0	Y	2021-05-06 20:47:59.065588	0	2021-05-06 20:47:59.065588	0	Toolbar Button	Toolbar buttons definitions in tabs in windows	\N	55	4	40	2	N	Y	72	\N	0	\N
\.


--
-- Data for Name: ad_table; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_table (ad_table_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_table_uu, name, description, help, tablename, isview, loadseq, issecurityenabled, isdeleteable, ishighvolume, ischangelog, istranslated, ad_extension_id, ispublic, accesslevel) FROM stdin;
1	0	0	Y	2020-01-22 20:37:00.425688	0	2021-01-14 20:39:04.361	0	\N	AD_Resource_Type	Type of resource available. Ex: Table / Window		AD_Resource_Type	N	\N	N	Y	N	N	N	0	N	6
6	0	0	Y	2020-02-05 22:39:17.884345	0	2021-01-14 19:38:32.18	0	\N	AD_Reference			AD_Reference	N	\N	N	Y	N	N	N	0	N	6
14	0	0	Y	2019-12-31 15:27:48.857112	0	2021-01-14 19:38:58.057	0	\N	AD_Tree	Identifies a Tree	\N	AD_Tree	N	90	N	Y	N	N	N	0	N	6
5	0	0	Y	2019-12-28 16:37:31.446967	0	2019-12-28 16:37:31.446967	0	1f7ed3a2-0b92-4479-904e-57dba4738da5	Column	Column in the table	\N	AD_Column	N	130	N	Y	N	N	N	0	N	4
15	0	0	Y	2019-12-31 15:38:38.514391	0	2021-01-14 19:39:01.452	0	\N	AD_TreeNode		\N	AD_TreeNode	N	145	N	Y	N	N	N	0	N	6
24	0	0	Y	2020-02-05 21:19:10.158173	0	2020-02-05 21:19:10.158173	0	\N	AD_ModelValidator	\N	\N	AD_ModelValidator	N	\N	N	Y	N	N	N	0	N	4
8	0	0	Y	2019-12-28 16:43:14.910969	0	2019-12-28 16:43:14.910969	0	723e492a-f944-40fc-a2d4-7aea9eaab8bd	AD_Tab	Tab within a Window	\N	AD_Tab	N	135	N	Y	N	N	N	0	N	4
7	0	0	Y	2019-12-28 03:52:23.48853	0	2019-12-28 03:52:23.48853	0	70a7e0ae-ca4c-4dbe-9e49-8d8e9a3833a3	AD_Window	Data entry or display window	\N	AD_Window	N	50	N	Y	N	N	N	0	N	4
12	0	0	Y	2019-12-31 16:02:26.980708	0	2019-12-31 16:02:26.980708	0	\N	AD_User	User within the system - Internal or Business Partner Contact		AD_User	N	80	N	Y	N	N	N	0	N	6
21	0	0	Y	2020-01-29 21:27:30.853768	0	2020-01-29 21:27:30.853768	0	\N	AD_Object_Access	\N	\N	AD_Object_Access	N	\N	N	Y	N	N	N	0	N	6
3	0	0	Y	2020-01-27 15:05:36.364778	0	2020-01-27 15:05:36.364778	0	\N	AD_OAuth2_Client_Token			AD_OAuth2_Client_Token	N	\N	N	Y	N	N	N	0	N	6
2	0	0	Y	2020-01-24 13:53:09.91057	0	2020-01-24 13:53:09.91057	0	\N	AD_OAuth2_Client			AD_OAuth2_Client	N	\N	N	Y	N	N	N	0	N	6
11	0	0	Y	2019-12-31 15:04:18.905714	0	2019-12-31 15:04:18.905714	0	\N	Client	Client/Tenant for this installation.	\N	AD_Client	N	40	N	Y	N	N	N	0	N	6
13	0	0	Y	2019-12-31 15:22:28.564688	0	2019-12-31 15:22:28.564688	0	\N	AD_Org	Organizational entity within client	\N	AD_Org	N	45	N	Y	N	N	N	0	N	6
9	0	0	Y	2019-12-28 16:43:55.3676	0	2019-12-28 16:43:55.3676	0	cda70656-e3cd-4cd8-9486-ef6183100f06	Field	Field on a database table	\N	AD_Field	N	140	N	Y	N	N	N	0	N	4
43	0	0	Y	2020-06-15 17:49:48.35124	0	2020-06-15 17:49:48.35124	0	\N	AD_User_App	\N	\N	AD_User_App	N	\N	N	Y	N	N	N	0	N	6
26	0	0	Y	2020-02-23 01:22:37.244846	0	2020-02-23 01:22:37.244846	0	\N	AD_Extension	\N	\N	AD_Extension	N	\N	N	Y	N	N	N	0	N	4
44	0	0	Y	2020-06-16 19:54:18.28775	0	2020-06-16 19:54:18.28775	0	\N	AD_Window_Access	\N	\N	AD_Window_Access	N	\N	N	Y	N	N	N	0	N	6
29	0	0	Y	2020-03-07 19:54:12.906447	0	2020-03-07 19:54:12.906447	0	\N	AD_MediaFormat	\N	\N	AD_MediaFormat	N	\N	N	Y	N	N	N	0	N	4
10	0	0	Y	2019-12-28 16:44:45.425171	0	2019-12-28 16:44:45.425171	0	f4c3242a-cede-4730-9323-3c0457ae6de5	AD_Message	System Message	\N	AD_Message	N	130	N	Y	N	N	Y	0	N	4
46	0	0	Y	2020-10-29 09:48:28.577669	0	2020-10-29 10:51:47.762	0	\N	AD_AppRule	\N	\N	AD_AppRule	N	\N	N	Y	N	N	N	0	N	6
47	0	0	Y	2020-10-29 09:52:51.671689	0	2020-10-29 13:58:38.899	0	\N	AD_Variable	\N	\N	AD_Variable	N	\N	N	Y	N	N	N	0	N	6
27	0	0	Y	2020-02-28 21:05:19.724712	0	2020-02-28 21:05:19.724712	0	\N	AD_ServiceProvider	\N	\N	AD_ServiceProvider	N	\N	N	Y	N	N	N	0	N	4
34	0	0	Y	2020-03-27 18:51:18.070096	0	2020-03-27 18:51:18.070096	0	\N	AD_User_Roles	\N	\N	AD_User_Roles	N	\N	N	Y	N	N	N	0	N	6
35	0	0	Y	2020-03-27 18:58:48.238042	0	2020-03-27 18:58:48.238042	0	\N	AD_Role	\N	\N	AD_Role	N	\N	N	Y	N	N	N	0	N	6
36	0	0	Y	2020-03-27 19:51:53.250578	0	2020-03-27 19:51:53.250578	0	\N	AD_OAuth_Client_Roles	\N	\N	AD_OAuth_Client_Roles	N	\N	N	Y	N	N	N	0	N	6
56	0	0	Y	2021-05-08 20:06:04.603468	0	2021-05-08 20:08:19.409	0	\N	AD_Process_Para	\N	\N	AD_Process_Para	N	\N	N	Y	N	N	N	0	N	6
30	0	0	Y	2020-03-07 19:54:28.961542	0	2020-03-07 19:54:28.961542	0	\N	AD_MediaFolder	\N	\N	AD_MediaFolder	N	\N	N	Y	N	N	N	0	N	6
28	0	0	Y	2020-03-07 19:50:02.359401	0	2020-03-07 19:50:02.359401	0	\N	AD_Media	\N	\N	AD_Media	N	\N	N	Y	N	N	N	0	N	6
41	0	0	Y	2020-04-16 09:17:56.690668	0	2020-04-16 09:17:56.690668	0	\N	AD_MailConfig	\N	\N	AD_MailConfig	N	\N	N	Y	N	N	N	0	N	6
31	0	0	Y	2020-03-07 20:38:31.418033	0	2020-03-07 20:38:31.418033	0	\N	AD_Attachment	\N	\N	AD_Attachment	N	\N	N	Y	N	N	N	0	N	6
32	0	0	Y	2020-03-14 13:39:59.177351	0	2020-03-14 13:39:59.177351	0	\N	AD_Language	\N	\N	AD_Language	N	\N	N	Y	N	N	N	0	Y	4
48	0	0	Y	2020-12-13 18:49:43.704461	0	2020-12-15 19:23:18.371	0	\N	AD_JobDefinition	\N	\N	AD_JobDefinition	N	\N	N	Y	N	N	N	0	N	4
50	0	0	Y	2020-12-28 13:08:52.616232	0	2020-12-28 14:20:38.793	0	\N	AD_Message_Trl	\N	\N	AD_Message_Trl	N	\N	N	Y	N	N	N	0	N	7
40	0	0	Y	2020-04-16 08:56:34.354575	0	2021-01-01 14:01:28.038	0	\N	AD_NotificationTemplate	\N	\N	AD_NotificationTemplate	N	\N	N	Y	N	N	Y	0	N	6
51	0	0	Y	2021-01-01 13:00:54.930254	0	2021-01-01 14:02:15.313	0	\N	AD_NotificationTemplate_Trl	\N	\N	AD_NotificationTemplate_Trl	N	\N	N	Y	N	N	N	0	N	6
4	0	0	Y	2020-01-17 18:58:36.134774	0	2021-01-30 16:22:34.745	0	0c9c5f93-56ea-4b9a-bd0c-8bc323edd8df	AD_Table	Table for the Fields	\N	AD_Table	N	55	N	Y	N	N	N	0	N	6
33	0	0	Y	2020-03-27 09:43:25.843312	0	2021-01-14 19:38:28.437	0	\N	AD_Ref_List	\N	\N	AD_Ref_List	N	\N	N	Y	N	N	N	0	N	6
45	0	0	Y	2020-09-14 20:05:49.333815	0	2021-01-14 19:38:44.056	0	\N	AD_SysConfig	\N	\N	AD_SysConfig	N	\N	N	Y	N	N	N	0	N	6
49	0	0	Y	2020-12-13 19:10:09.462313	0	2021-01-14 19:50:20.928	0	\N	AD_CronJob	\N	\N	AD_CronJob	N	\N	N	Y	N	N	N	0	N	6
52	0	0	Y	2021-02-06 15:23:45.315687	0	2021-03-08 17:07:32.142	0	\N	AD_App	\N	\N	AD_App	N	\N	N	Y	N	N	N	0	N	6
53	0	0	Y	2021-04-26 18:33:22.96857	0	2021-04-26 18:34:18.56	0	\N	AD_Scripting	\N	\N	AD_Scripting	N	\N	N	Y	N	N	N	0	N	6
54	0	0	Y	2021-05-06 20:39:22.376612	0	2021-05-06 20:39:58.615	0	\N	AD_Process	\N	\N	AD_Process	N	\N	N	Y	N	N	N	0	N	6
55	0	0	Y	2021-05-06 20:39:33.474519	0	2021-05-06 20:42:24.665	0	\N	AD_ToolBarButton	\N	\N	AD_ToolBarButton	N	\N	N	Y	N	N	N	0	N	4
\.


--
-- Data for Name: ad_table_trl; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_table_trl (ad_table_id, ad_language, name) FROM stdin;
4	pt_BR	Tabela
\.


--
-- Data for Name: ad_toolbarbutton; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_toolbarbutton (ad_toolbarbutton_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, icon, description, help, ad_process_id, actionname, ad_tab_id, islinkedtoselectedrecord, ad_toolbarbutton_uu) FROM stdin;
2	0	0	Y	2021-05-06 21:20:39.06128	0	2021-05-08 23:01:24.082	0	Create Table from Database	fa fa-cog	Create Table from Database	\N	1	Sync Database	2	Y	8b2174af-8315-4ecf-bb17-6ad6335de226
    \.


--
-- Data for Name: ad_tree; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_tree (ad_tree_id, ad_client_id, ad_org_id, created, createdby, updated, updatedby, isactive, name, description, isdefault, ad_tree_uu) FROM stdin;
0	0	0	2019-12-31 15:46:20.424676	0	2019-12-31 15:46:20.424676	0	Y	Default	Main Menu	Y	9e9f294f-62c2-4281-8301-f230a41de6fb
\.


--
-- Data for Name: ad_treenode; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_treenode (ad_tree_id, ad_treenode_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_treenode_parent_id, name, issummary, seqno, ad_treenode_uu, ad_window_id) FROM stdin;
0	9	0	0	Y	2020-03-25 21:03:07.100884	0	2020-03-25 21:03:07.100884	0	1	References	N	1	cdcf45dc-2f7d-4906-b51c-fb943cb3d3b6	3
0	5	0	0	Y	2020-03-25 21:00:18.229869	0	2020-03-25 21:00:18.229869	0	1	Client	N	1	aaa4ec48-e15c-4438-839d-81a831880f4a	7
0	6	0	0	Y	2020-03-25 21:01:50.714109	0	2020-03-25 21:01:50.714109	0	1	Language	N	1	90ba4a3d-c8dd-4dcb-9c5c-3487054175e4	8
0	10	0	0	Y	2020-03-25 21:03:22.161795	0	2020-03-25 21:03:22.161795	0	1	Extensions	N	1	999f3d82-db42-4575-a450-0aa4f13a8d5d	11
0	2	0	0	Y	2019-12-31 19:15:59.788153	0	2019-12-31 19:15:59.788153	0	1	Table and Column	N	1	3a9d297b-e017-4a05-9298-7844a4865b4b	2
0	3	0	0	Y	2019-12-31 19:15:59.788153	0	2019-12-31 19:15:59.788153	0	1	Window, Tab & Field	N	1	de965ece-2616-40f0-a5e5-70cc01913b1b	4
0	7	0	0	Y	2020-03-25 21:02:08.705424	0	2020-03-25 21:02:08.705424	0	1	Menu	N	1	61043924-ff31-4a46-9b63-99101ef17a7b	9
0	8	0	0	Y	2020-03-25 21:02:46.328442	0	2020-03-25 21:02:46.328442	0	1	Service Provider	N	1	7515ae37-d734-4f57-b9b9-d2ee493ca78a	10
0	21	0	0	Y	2020-04-16 10:06:54.863985	0	2020-04-16 10:06:54.863985	0	1	Message Template	N	1	\N	18
0	22	0	0	Y	2020-04-16 10:52:26.99729	0	2020-04-16 10:52:26.99729	0	1	Mail Config	N	1	\N	19
0	15	0	0	Y	2020-03-25 21:09:42.072373	0	2020-03-25 21:09:42.072373	0	11	Media Format	N	1	b2d5ea83-e63d-4f5c-93a1-73cc085b2c2f	14
0	14	0	0	Y	2020-03-25 21:09:29.665333	0	2020-03-25 21:09:29.665333	0	11	Media Folder	N	1	ea30b93c-0273-46de-a86e-f054605427d4	13
0	17	0	0	Y	2020-03-25 21:11:03.116532	0	2020-03-25 21:11:03.116532	0	12	Roles	N	1	663a1bf1-ad2d-40db-8c9a-77ad45cc7526	16
0	16	0	0	Y	2020-03-25 21:10:46.924958	0	2020-03-25 21:10:46.924958	0	12	User	N	1	42a35d23-1618-4253-a7c3-1b2ce6af4e39	15
0	18	0	0	Y	2020-03-25 21:12:02.838048	0	2020-03-25 21:12:02.838048	0	12	OAuth Client	N	1	cbf895e9-1831-4ed7-9b8d-bec5bd2d737e	17
0	0	0	0	Y	2020-03-25 20:31:29.955299	0	2020-03-25 20:31:29.955299	0	\N	Root	Y	-1	dae35fd3-88e4-42e0-b3b8-adad25c7ae43	\N
0	12	0	0	Y	2020-03-25 21:06:55.710376	0	2020-12-15 19:01:16.483	0	0	Access Control	Y	30	0ea7055e-d487-4221-866f-d8ba293f63ed	17
0	11	0	0	Y	2020-03-25 21:06:34.269485	0	2020-12-15 19:01:21.436	0	0	Medias	Y	20	af7f4162-52c6-4e94-a33e-59c2499be255	\N
0	1	0	0	Y	2019-12-31 19:15:59.788153	0	2020-12-15 19:01:25.435	0	0	System Admin	Y	10	1b792466-c9cd-4ffc-b951-cf6e8bd918a8	\N
0	23	0	0	Y	2020-09-14 20:16:55.742262	0	2020-09-14 20:16:55.742262	0	1	System	N	2	e1548f74-4d3f-4222-8e94-873d1fa46927	20
0	24	0	0	Y	2020-10-29 10:15:57.709163	0	2020-10-29 10:17:02.958	0	1	Variables	N	1	9f9186ae-3ea2-4a96-93ed-8fc33dae84a9	21
0	25	0	0	Y	2020-12-15 17:58:38.457527	0	2020-12-15 17:58:38.457527	0	1	Job Definition	N	1	c7adea63-e572-4843-ad86-a7db6ff93218	22
0	26	0	0	Y	2020-12-28 13:06:57.257264	0	2020-12-28 13:06:57.257264	0	1	System Messages	N	1	4fe53951-5d96-4de3-9314-1157cf91e7a9	5
0	27	0	0	Y	2021-04-28 11:51:18.3624	0	2021-04-28 11:51:42.709	0	1	Scripting	N	1	bee400c0-c458-4cd5-9d5a-9a50361babcf	23
0	28	0	0	Y	2021-05-06 21:16:16.749821	0	2021-05-06 21:18:04.514	0	1	Process	N	1	4f5eaa17-203c-41f5-b233-80dd1b7595c1	24
\.


--
-- Data for Name: ad_user; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_user (ad_user_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, emailuser, userpin, ad_user_uu, islocked, dateaccountlocked, datelastlogin, isaccountverified, name, userlevel, isadmin, isviewonlyactiverecords) FROM stdin;
0	0	0	Y	2019-12-31 16:11:13.618921	0	2021-05-11 17:58:11.782	0	admin@cadre.com	a45da96d0bf6575970f2d27af22be28a	da12c119-53f0-4def-be29-606472814543	N	\N	2021-05-11 17:58:11.782	Y	CadreAdmin	S  	Y	N
\.


--
-- Data for Name: ad_user_app; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_user_app (ad_user_id, ad_user_app_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_user_app_uu, ad_app_id) FROM stdin;
0	1	0	0	Y	2020-06-15 18:12:07.015565	0	2020-06-15 18:12:07.015565	0	468a4f00-ebe5-4df8-8229-68b5972094f3	1
\.


--
-- Data for Name: ad_user_roles; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_user_roles (ad_user_id, ad_role_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_user_roles_uu, ad_user_roles_id) FROM stdin;
\.


--
-- Data for Name: ad_variable; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_variable (ad_variable_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_reference_id, classname, columnsql, constantvalue, description, type, value, ad_variable_uu) FROM stdin;
\.


--
-- Data for Name: ad_window; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_window (ad_window_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, help, ad_window_uu, ad_extension_id) FROM stdin;
1	0	0	Y	2020-01-22 20:43:20.564317	0	2020-01-22 20:43:20.564317	0	Resource Type			\N	0
2	0	0	Y	2019-12-29 19:45:04.143781	0	2019-12-29 19:45:04.143781	0	Table and Column	Maintain Tables and Columns	\N	e407e1b9-958f-4550-9ffa-ba28a34e603f	0
3	0	0	Y	2019-12-29 19:45:20.230062	0	2019-12-29 19:45:20.230062	100	Reference	Maintain System References	\N	1f3b9147-13bb-44d1-b3bf-b5167fa51255	0
4	0	0	Y	2019-12-29 19:45:33.546449	0	2019-12-29 19:45:33.546449	0	Window, Tab & Field	Maintain Windows, Tabs & Fields	\N	f1f8bd40-7c71-4ace-9a45-1442a2dfd8a9	0
9	0	0	Y	2020-03-25 21:22:20.632011	0	2020-03-25 21:22:20.632011	0	Menu			\N	0
13	0	0	Y	2020-03-25 21:23:04.975822	0	2020-03-25 21:23:04.975822	0	Media Folder			\N	0
14	0	0	Y	2020-03-25 21:23:16.807764	0	2020-03-25 21:23:16.807764	0	Media Format			\N	0
15	0	0	Y	2020-03-25 21:23:29.56301	0	2020-03-25 21:23:29.56301	0	User & Roles			\N	0
16	0	0	Y	2020-03-25 21:23:37.862431	0	2020-03-25 21:23:37.862431	0	Roles			\N	0
8	0	0	Y	2020-03-25 21:22:12.782465	0	2020-03-25 21:22:12.782465	0	Language			\N	0
11	0	0	Y	2020-03-25 21:22:41.038986	0	2020-03-25 21:22:41.038986	0	Extension			\N	0
10	0	0	Y	2020-03-25 21:22:32.25238	0	2020-03-25 21:22:32.25238	0	Service Provider			\N	0
7	0	0	Y	2020-03-25 21:22:03.010376	0	2020-03-25 21:22:03.010376	0	Client	\N		\N	0
19	0	0	Y	2020-04-16 09:26:17.273517	0	2020-04-16 09:26:17.273517	0	Mail Config	\N	\N	\N	0
18	0	0	Y	2020-04-16 09:03:31.947555	0	2020-04-16 09:03:31.947555	0	Message Template	\N	\N	\N	0
20	0	0	Y	2020-09-14 20:12:32.607019	0	2020-09-14 20:12:32.607019	0	System	\N	\N	\N	0
21	0	0	Y	2020-10-29 10:11:10.174144	0	2020-10-29 10:11:10.174144	0	Variables	\N	\N	\N	0
22	0	0	Y	2020-12-15 17:49:06.394423	0	2020-12-15 17:49:06.394423	0	Job Definition	\N	\N	\N	0
5	0	0	Y	2019-12-29 19:45:42.290427	0	2020-12-28 14:10:59.943	0	System Messages	Maintain Information and Error Messages	\N	6557d21a-0d66-4eba-86b8-e820feba3126	0
17	0	0	Y	2020-03-25 21:23:58.920204	0	2021-02-06 16:31:41.983	0	App / Client / Roles			\N	0
23	0	0	Y	2021-04-28 11:46:38.672204	0	2021-04-28 11:46:38.672204	0	Scripting	\N	\N	\N	0
24	0	0	Y	2021-05-06 21:04:33.085094	0	2021-05-06 21:04:33.085094	0	Process	\N	\N	\N	0
\.


--
-- Data for Name: ad_window_access; Type: TABLE DATA; Schema: cadre; Owner: cadre
--

COPY cadre.ad_window_access (ad_window_access_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_role_id, ad_window_id, readonly, ad_window_access_uu) FROM stdin;
\.


--
-- Name: ad_app_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_app_sq', 1, true);


--
-- Name: ad_apprule_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_apprule_sq', 1, false);


--
-- Name: ad_attachment_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_attachment_sq', 1, false);


--
-- Name: ad_client_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_client_sq', 1, false);


--
-- Name: ad_column_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_column_sq', 754, true);


--
-- Name: ad_cronjob_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_cronjob_sq', 1, true);


--
-- Name: ad_extension_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_extension_sq', 1, false);


--
-- Name: ad_field_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_field_sq', 436, true);


--
-- Name: ad_jobdefinition_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_jobdefinition_sq', 7, true);


--
-- Name: ad_language_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_language_sq', 50017, false);


--
-- Name: ad_loginmodule_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_loginmodule_sq', 1, true);


--
-- Name: ad_mailconfig_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_mailconfig_sq', 2, false);


--
-- Name: ad_media_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_media_sq', 5, false);


--
-- Name: ad_mediafolder_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_mediafolder_sq', 3, false);


--
-- Name: ad_mediaformat_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_mediaformat_sq', 70, false);


--
-- Name: ad_message_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_message_sq', 21, false);


--
-- Name: ad_message_trl_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_message_trl_sq', 8, true);


--
-- Name: ad_modelvalidator_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_modelvalidator_sq', 2, false);


--
-- Name: ad_notificationtemplate_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_notificationtemplate_sq', 3, false);


--
-- Name: ad_oauth2_client_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_oauth2_client_sq', 3, false);


--
-- Name: ad_oauth2_client_token_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_oauth2_client_token_sq', 133, true);


--
-- Name: ad_oauth_client_roles_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_oauth_client_roles_sq', 2, false);


--
-- Name: ad_object_access_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_object_access_sq', 1, false);


--
-- Name: ad_org_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_org_sq', 1, false);


--
-- Name: ad_process_para_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_process_para_sq', 6, true);


--
-- Name: ad_process_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_process_sq', 1, true);


--
-- Name: ad_ref_list_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_ref_list_sq', 30, true);


--
-- Name: ad_reference_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_reference_sq', 51, true);


--
-- Name: ad_resource_type_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_resource_type_sq', 5, false);


--
-- Name: ad_role_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_role_sq', 3, false);


--
-- Name: ad_scripting_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_scripting_sq', 7, true);


--
-- Name: ad_serviceprovider_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_serviceprovider_sq', 14, false);


--
-- Name: ad_sysconfig_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_sysconfig_sq', 3, false);


--
-- Name: ad_tab_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_tab_sq', 47, true);


--
-- Name: ad_table_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_table_sq', 56, true);


--
-- Name: ad_toolbarbutton_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_toolbarbutton_sq', 2, true);


--
-- Name: ad_tree_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_tree_sq', 1, false);


--
-- Name: ad_treenode_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_treenode_sq', 28, true);


--
-- Name: ad_user_app_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_user_app_sq', 2, false);


--
-- Name: ad_user_roles_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_user_roles_sq', 1, false);


--
-- Name: ad_user_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_user_sq', 1, false);


--
-- Name: ad_variable_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_variable_sq', 1, false);


--
-- Name: ad_window_access_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_window_access_sq', 1, false);


--
-- Name: ad_window_sq; Type: SEQUENCE SET; Schema: cadre; Owner: cadre
--

SELECT pg_catalog.setval('cadre.ad_window_sq', 24, true);


--
-- Name: ad_app ad_app_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_app
    ADD CONSTRAINT ad_app_pkey PRIMARY KEY (ad_app_id);


--
-- Name: ad_app ad_app_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_app
    ADD CONSTRAINT ad_app_uu_idx UNIQUE (ad_app_uu);


--
-- Name: ad_apprule ad_apprule_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_apprule
    ADD CONSTRAINT ad_apprule_pkey PRIMARY KEY (ad_apprule_id);


--
-- Name: ad_apprule ad_apprule_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_apprule
    ADD CONSTRAINT ad_apprule_uu_idx UNIQUE (ad_apprule_uu);


--
-- Name: ad_attachment ad_attachment_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_attachment
    ADD CONSTRAINT ad_attachment_pkey PRIMARY KEY (ad_attachment_id);


--
-- Name: ad_attachment ad_attachment_record; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_attachment
    ADD CONSTRAINT ad_attachment_record UNIQUE (ad_table_id, ad_record_id);


--
-- Name: ad_attachment ad_attachment_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_attachment
    ADD CONSTRAINT ad_attachment_uu_idx UNIQUE (ad_attachment_uu);


--
-- Name: ad_client ad_client_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_client
    ADD CONSTRAINT ad_client_pkey PRIMARY KEY (ad_client_id);


--
-- Name: ad_client ad_client_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_client
    ADD CONSTRAINT ad_client_uu_idx UNIQUE (ad_client_uu);


--
-- Name: ad_client ad_client_value; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_client
    ADD CONSTRAINT ad_client_value UNIQUE (value);


--
-- Name: ad_column ad_column_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_column
    ADD CONSTRAINT ad_column_pkey PRIMARY KEY (ad_column_id);


--
-- Name: ad_column ad_column_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_column
    ADD CONSTRAINT ad_column_uu_idx UNIQUE (ad_column_uu);


--
-- Name: ad_cronjob ad_cronjob_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_cronjob
    ADD CONSTRAINT ad_cronjob_pkey PRIMARY KEY (ad_cronjob_id);


--
-- Name: ad_cronjob ad_cronjob_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_cronjob
    ADD CONSTRAINT ad_cronjob_uu_idx UNIQUE (ad_cronjob_uu);


--
-- Name: ad_extension ad_extension_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_extension
    ADD CONSTRAINT ad_extension_pkey PRIMARY KEY (ad_extension_id);


--
-- Name: ad_extension ad_extension_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_extension
    ADD CONSTRAINT ad_extension_uu_idx UNIQUE (ad_extension_uu);


--
-- Name: ad_extension ad_extension_value; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_extension
    ADD CONSTRAINT ad_extension_value UNIQUE (value);


--
-- Name: ad_field ad_field_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_field
    ADD CONSTRAINT ad_field_pkey PRIMARY KEY (ad_field_id);


--
-- Name: ad_field ad_field_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_field
    ADD CONSTRAINT ad_field_uu_idx UNIQUE (ad_field_uu);


--
-- Name: ad_jobdefinition ad_jobdefinition_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_jobdefinition
    ADD CONSTRAINT ad_jobdefinition_pkey PRIMARY KEY (ad_jobdefinition_id);


--
-- Name: ad_jobdefinition ad_jobdefinition_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_jobdefinition
    ADD CONSTRAINT ad_jobdefinition_uu_idx UNIQUE (ad_jobdefinition_uu);


--
-- Name: ad_language ad_language_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_language
    ADD CONSTRAINT ad_language_pkey PRIMARY KEY (ad_language);


--
-- Name: ad_language ad_language_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_language
    ADD CONSTRAINT ad_language_uu_idx UNIQUE (ad_language_uu);


--
-- Name: ad_mailconfig ad_mailconfig_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_mailconfig
    ADD CONSTRAINT ad_mailconfig_pkey PRIMARY KEY (ad_mailconfig_id);


--
-- Name: ad_mailconfig ad_mailconfig_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_mailconfig
    ADD CONSTRAINT ad_mailconfig_uu_idx UNIQUE (ad_mailconfig_uu);


--
-- Name: ad_media ad_media_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_media
    ADD CONSTRAINT ad_media_pkey PRIMARY KEY (ad_media_id);


--
-- Name: ad_media ad_media_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_media
    ADD CONSTRAINT ad_media_uu_idx UNIQUE (ad_media_uu);


--
-- Name: ad_media ad_media_value; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_media
    ADD CONSTRAINT ad_media_value UNIQUE (value);


--
-- Name: ad_mediafolder ad_mediafolder_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_mediafolder
    ADD CONSTRAINT ad_mediafolder_pkey PRIMARY KEY (ad_mediafolder_id);


--
-- Name: ad_mediafolder ad_mediafolder_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_mediafolder
    ADD CONSTRAINT ad_mediafolder_uu_idx UNIQUE (ad_mediafolder_uu);


--
-- Name: ad_mediafolder ad_mediafolder_value; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_mediafolder
    ADD CONSTRAINT ad_mediafolder_value UNIQUE (name);


--
-- Name: ad_mediaformat ad_mediaformat_extension; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_mediaformat
    ADD CONSTRAINT ad_mediaformat_extension UNIQUE (extension);


--
-- Name: ad_mediaformat ad_mediaformat_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_mediaformat
    ADD CONSTRAINT ad_mediaformat_pkey PRIMARY KEY (ad_mediaformat_id);


--
-- Name: ad_mediaformat ad_mediaformat_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_mediaformat
    ADD CONSTRAINT ad_mediaformat_uu_idx UNIQUE (ad_mediaformat_uu);


--
-- Name: ad_message ad_message_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_message
    ADD CONSTRAINT ad_message_pkey PRIMARY KEY (ad_message_id);


--
-- Name: ad_message_trl ad_message_trl_unique; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_message_trl
    ADD CONSTRAINT ad_message_trl_unique UNIQUE (ad_message_id, ad_language);


--
-- Name: ad_message ad_message_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_message
    ADD CONSTRAINT ad_message_uu_idx UNIQUE (ad_message_uu);


--
-- Name: ad_message ad_message_value; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_message
    ADD CONSTRAINT ad_message_value UNIQUE (value);


--
-- Name: ad_modelvalidator ad_modelvalidator_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_modelvalidator
    ADD CONSTRAINT ad_modelvalidator_pkey PRIMARY KEY (ad_modelvalidator_id);


--
-- Name: ad_modelvalidator ad_modelvalidator_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_modelvalidator
    ADD CONSTRAINT ad_modelvalidator_uu_idx UNIQUE (ad_modelvalidator_uu);


--
-- Name: ad_notificationtemplate ad_notificationtemplate_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_notificationtemplate
    ADD CONSTRAINT ad_notificationtemplate_pkey PRIMARY KEY (ad_notificationtemplate_id);


--
-- Name: ad_notificationtemplate_trl ad_notificationtemplate_unique; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_notificationtemplate_trl
    ADD CONSTRAINT ad_notificationtemplate_unique UNIQUE (ad_notificationtemplate_id, ad_language);


--
-- Name: ad_notificationtemplate ad_notificationtemplate_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_notificationtemplate
    ADD CONSTRAINT ad_notificationtemplate_uu_idx UNIQUE (ad_notificationtemplate_uu);


--
-- Name: ad_oauth2_client ad_oauth2_client_clientid; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth2_client
    ADD CONSTRAINT ad_oauth2_client_clientid UNIQUE (clientid);


--
-- Name: ad_oauth2_client ad_oauth2_client_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth2_client
    ADD CONSTRAINT ad_oauth2_client_pkey PRIMARY KEY (ad_oauth2_client_id);


--
-- Name: ad_oauth2_client_token ad_oauth2_client_token_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth2_client_token
    ADD CONSTRAINT ad_oauth2_client_token_pkey PRIMARY KEY (ad_oauth2_client_token_id);


--
-- Name: ad_oauth2_client ad_oauth2_client_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth2_client
    ADD CONSTRAINT ad_oauth2_client_uu_idx UNIQUE (ad_oauth2_client_uu);


--
-- Name: ad_oauth_client_roles ad_oauth_client_roles_nkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth_client_roles
    ADD CONSTRAINT ad_oauth_client_roles_nkey UNIQUE (ad_role_id, ad_oauth2_client_id);


--
-- Name: ad_oauth_client_roles ad_oauth_client_roles_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth_client_roles
    ADD CONSTRAINT ad_oauth_client_roles_pkey PRIMARY KEY (ad_oauth_client_roles_id);


--
-- Name: ad_oauth_client_roles ad_oauth_client_roles_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth_client_roles
    ADD CONSTRAINT ad_oauth_client_roles_uu_idx UNIQUE (ad_oauth_client_roles_uu);


--
-- Name: ad_object_access ad_object_access_nkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_object_access
    ADD CONSTRAINT ad_object_access_nkey UNIQUE (ad_role_id, ad_resource_type_id, value);


--
-- Name: ad_object_access ad_object_access_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_object_access
    ADD CONSTRAINT ad_object_access_pkey PRIMARY KEY (ad_object_access_id);


--
-- Name: ad_object_access ad_object_access_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_object_access
    ADD CONSTRAINT ad_object_access_uu_idx UNIQUE (ad_object_access_uu);


--
-- Name: ad_org ad_org_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_org
    ADD CONSTRAINT ad_org_pkey PRIMARY KEY (ad_org_id);


--
-- Name: ad_org ad_org_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_org
    ADD CONSTRAINT ad_org_uu_idx UNIQUE (ad_org_uu);


--
-- Name: ad_org ad_org_value; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_org
    ADD CONSTRAINT ad_org_value UNIQUE (value);


--
-- Name: ad_process_para ad_process_para_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_process_para
    ADD CONSTRAINT ad_process_para_uu_idx UNIQUE (ad_process_para_uu);


--
-- Name: ad_process ad_process_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_process
    ADD CONSTRAINT ad_process_pkey PRIMARY KEY (ad_process_id);


--
-- Name: ad_process ad_process_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_process
    ADD CONSTRAINT ad_process_uu_idx UNIQUE (ad_process_uu);


--
-- Name: ad_ref_list ad_ref_list_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_ref_list
    ADD CONSTRAINT ad_ref_list_pkey PRIMARY KEY (ad_ref_list_id);


--
-- Name: ad_ref_list ad_ref_list_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_ref_list
    ADD CONSTRAINT ad_ref_list_uu_idx UNIQUE (ad_ref_list_uu);


--
-- Name: ad_ref_list ad_ref_list_value; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_ref_list
    ADD CONSTRAINT ad_ref_list_value UNIQUE (ad_ref_list_id, value);


--
-- Name: ad_reference ad_reference_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_reference
    ADD CONSTRAINT ad_reference_pkey PRIMARY KEY (ad_reference_id);


--
-- Name: ad_reference ad_reference_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_reference
    ADD CONSTRAINT ad_reference_uu_idx UNIQUE (ad_reference_uu);


--
-- Name: ad_resource_type ad_resource_type_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_resource_type
    ADD CONSTRAINT ad_resource_type_pkey PRIMARY KEY (ad_resource_type_id);


--
-- Name: ad_resource_type ad_resource_type_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_resource_type
    ADD CONSTRAINT ad_resource_type_uu_idx UNIQUE (ad_resource_type_uu);


--
-- Name: ad_role ad_role_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_role
    ADD CONSTRAINT ad_role_pkey PRIMARY KEY (ad_role_id);


--
-- Name: ad_role ad_role_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_role
    ADD CONSTRAINT ad_role_uu_idx UNIQUE (ad_role_uu);


--
-- Name: ad_scripting ad_scripting_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_scripting
    ADD CONSTRAINT ad_scripting_pkey PRIMARY KEY (ad_scripting_id);


--
-- Name: ad_scripting ad_scripting_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_scripting
    ADD CONSTRAINT ad_scripting_uu_idx UNIQUE (ad_scripting_uu);


--
-- Name: ad_serviceprovider ad_serviceprovider_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_serviceprovider
    ADD CONSTRAINT ad_serviceprovider_pkey PRIMARY KEY (ad_serviceprovider_id);


--
-- Name: ad_serviceprovider ad_serviceprovider_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_serviceprovider
    ADD CONSTRAINT ad_serviceprovider_uu_idx UNIQUE (ad_serviceprovider_uu);


--
-- Name: ad_serviceprovider ad_serviceprovider_value; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_serviceprovider
    ADD CONSTRAINT ad_serviceprovider_value UNIQUE (value);


--
-- Name: ad_sysconfig ad_sysconfig_nk; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_sysconfig
    ADD CONSTRAINT ad_sysconfig_nk UNIQUE (ad_client_id, ad_org_id, name);


--
-- Name: ad_sysconfig ad_sysconfig_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_sysconfig
    ADD CONSTRAINT ad_sysconfig_pkey PRIMARY KEY (ad_sysconfig_id);


--
-- Name: ad_sysconfig ad_sysconfig_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_sysconfig
    ADD CONSTRAINT ad_sysconfig_uu_idx UNIQUE (ad_sysconfig_uu);


--
-- Name: ad_tab ad_tab_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_tab
    ADD CONSTRAINT ad_tab_pkey PRIMARY KEY (ad_tab_id);


--
-- Name: ad_tab ad_tab_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_tab
    ADD CONSTRAINT ad_tab_uu_idx UNIQUE (ad_tab_uu);


--
-- Name: ad_table ad_table_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_table
    ADD CONSTRAINT ad_table_pkey PRIMARY KEY (ad_table_id);


--
-- Name: ad_table ad_table_tablename; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_table
    ADD CONSTRAINT ad_table_tablename UNIQUE (tablename);


--
-- Name: ad_table ad_table_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_table
    ADD CONSTRAINT ad_table_uu_idx UNIQUE (ad_table_uu);


--
-- Name: ad_toolbarbutton ad_toolbarbutton_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_toolbarbutton
    ADD CONSTRAINT ad_toolbarbutton_pkey PRIMARY KEY (ad_toolbarbutton_id);


--
-- Name: ad_toolbarbutton ad_toolbarbutton_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_toolbarbutton
    ADD CONSTRAINT ad_toolbarbutton_uu_idx UNIQUE (ad_toolbarbutton_uu);


--
-- Name: ad_tree ad_tree_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_tree
    ADD CONSTRAINT ad_tree_pkey PRIMARY KEY (ad_tree_id);


--
-- Name: ad_tree ad_tree_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_tree
    ADD CONSTRAINT ad_tree_uu_idx UNIQUE (ad_tree_uu);


--
-- Name: ad_treenode ad_treenode_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_treenode
    ADD CONSTRAINT ad_treenode_pkey PRIMARY KEY (ad_treenode_id);


--
-- Name: ad_treenode ad_treenode_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_treenode
    ADD CONSTRAINT ad_treenode_uu_idx UNIQUE (ad_treenode_uu);


--
-- Name: ad_user_app ad_user_app_id_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user_app
    ADD CONSTRAINT ad_user_app_id_pkey PRIMARY KEY (ad_user_app_id);


--
-- Name: ad_user_app ad_user_app_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user_app
    ADD CONSTRAINT ad_user_app_uu_idx UNIQUE (ad_user_app_uu);


--
-- Name: ad_user ad_user_emailuser; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user
    ADD CONSTRAINT ad_user_emailuser UNIQUE (emailuser);


--
-- Name: ad_user ad_user_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user
    ADD CONSTRAINT ad_user_pkey PRIMARY KEY (ad_user_id);


--
-- Name: ad_user_roles ad_user_roles_nkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user_roles
    ADD CONSTRAINT ad_user_roles_nkey UNIQUE (ad_role_id, ad_user_id);


--
-- Name: ad_user_roles ad_user_roles_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user_roles
    ADD CONSTRAINT ad_user_roles_pkey PRIMARY KEY (ad_user_roles_id);


--
-- Name: ad_user_roles ad_user_roles_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user_roles
    ADD CONSTRAINT ad_user_roles_uu_idx UNIQUE (ad_user_roles_uu);


--
-- Name: ad_user ad_user_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user
    ADD CONSTRAINT ad_user_uu_idx UNIQUE (ad_user_uu);


--
-- Name: ad_variable ad_variable_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_variable
    ADD CONSTRAINT ad_variable_pkey PRIMARY KEY (ad_variable_id);


--
-- Name: ad_variable ad_variable_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_variable
    ADD CONSTRAINT ad_variable_uu_idx UNIQUE (ad_variable_uu);


--
-- Name: ad_variable ad_variable_value; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_variable
    ADD CONSTRAINT ad_variable_value UNIQUE (value);


--
-- Name: ad_window_access ad_window_access_id_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_window_access
    ADD CONSTRAINT ad_window_access_id_pkey PRIMARY KEY (ad_window_access_id);


--
-- Name: ad_window_access ad_window_access_nkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_window_access
    ADD CONSTRAINT ad_window_access_nkey UNIQUE (ad_role_id, ad_window_id);


--
-- Name: ad_window_access ad_window_access_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_window_access
    ADD CONSTRAINT ad_window_access_uu_idx UNIQUE (ad_window_access_uu);


--
-- Name: ad_window ad_window_pkey; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_window
    ADD CONSTRAINT ad_window_pkey PRIMARY KEY (ad_window_id);


--
-- Name: ad_window ad_window_uu_idx; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_window
    ADD CONSTRAINT ad_window_uu_idx UNIQUE (ad_window_uu);


--
-- Name: ad_column adcolumn_tableid_columnanme; Type: CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_column
    ADD CONSTRAINT adcolumn_tableid_columnanme UNIQUE (ad_table_id, columnname);


--
-- Name: ad_process_para_idx; Type: INDEX; Schema: cadre; Owner: cadre
--

CREATE UNIQUE INDEX ad_process_para_idx ON cadre.ad_process_para USING btree (ad_process_para_uu);


--
-- Name: ad_media ad_media_admediafolder; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_media
    ADD CONSTRAINT ad_media_admediafolder FOREIGN KEY (ad_mediafolder_id) REFERENCES cadre.ad_mediafolder(ad_mediafolder_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_media ad_media_admediaformat; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_media
    ADD CONSTRAINT ad_media_admediaformat FOREIGN KEY (ad_mediaformat_id) REFERENCES cadre.ad_mediaformat(ad_mediaformat_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_serviceprovider ad_serviceprovider_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_serviceprovider
    ADD CONSTRAINT ad_serviceprovider_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_apprule adapprule_adapp; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_apprule
    ADD CONSTRAINT adapprule_adapp FOREIGN KEY (ad_app_id) REFERENCES cadre.ad_app(ad_app_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_apprule adapprule_adtable; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_apprule
    ADD CONSTRAINT adapprule_adtable FOREIGN KEY (ad_table_id) REFERENCES cadre.ad_table(ad_table_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_client adclient_adlanguage; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_client
    ADD CONSTRAINT adclient_adlanguage FOREIGN KEY (ad_language) REFERENCES cadre.ad_language(ad_language) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_client adclient_admailconfig; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_client
    ADD CONSTRAINT adclient_admailconfig FOREIGN KEY (ad_mailconfig_id) REFERENCES cadre.ad_mailconfig(ad_mailconfig_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_client adclient_adtree; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_client
    ADD CONSTRAINT adclient_adtree FOREIGN KEY (ad_tree_id) REFERENCES cadre.ad_tree(ad_tree_id);


--
-- Name: ad_column adcolumn_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_column
    ADD CONSTRAINT adcolumn_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_column adcolumn_adreference; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_column
    ADD CONSTRAINT adcolumn_adreference FOREIGN KEY (ad_reference_id) REFERENCES cadre.ad_reference(ad_reference_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_column adcolumn_adreferencevalue; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_column
    ADD CONSTRAINT adcolumn_adreferencevalue FOREIGN KEY (ad_reference_value_id) REFERENCES cadre.ad_reference(ad_reference_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_column adcolumn_adtable; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_column
    ADD CONSTRAINT adcolumn_adtable FOREIGN KEY (ad_table_id) REFERENCES cadre.ad_table(ad_table_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_field adfield_adcolumn; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_field
    ADD CONSTRAINT adfield_adcolumn FOREIGN KEY (ad_column_id) REFERENCES cadre.ad_column(ad_column_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_field adfield_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_field
    ADD CONSTRAINT adfield_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_process_para adfield_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_process_para
    ADD CONSTRAINT adfield_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_field adfield_adtab; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_field
    ADD CONSTRAINT adfield_adtab FOREIGN KEY (ad_tab_id) REFERENCES cadre.ad_tab(ad_tab_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_jobdefinition adjobdefinition_adscripting; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_jobdefinition
    ADD CONSTRAINT adjobdefinition_adscripting FOREIGN KEY (ad_scripting_id) REFERENCES cadre.ad_scripting(ad_scripting_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_message_trl admessagetrl_adlanguage; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_message_trl
    ADD CONSTRAINT admessagetrl_adlanguage FOREIGN KEY (ad_language) REFERENCES cadre.ad_language(ad_language) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_modelvalidator admodelvalidator_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_modelvalidator
    ADD CONSTRAINT admodelvalidator_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_modelvalidator admodelvalidator_adtable; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_modelvalidator
    ADD CONSTRAINT admodelvalidator_adtable FOREIGN KEY (ad_table_id) REFERENCES cadre.ad_table(ad_table_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_notificationtemplate_trl adnotifitemplatetrl_adlanguage; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_notificationtemplate_trl
    ADD CONSTRAINT adnotifitemplatetrl_adlanguage FOREIGN KEY (ad_language) REFERENCES cadre.ad_language(ad_language) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_oauth2_client adoauth2client_adapp; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth2_client
    ADD CONSTRAINT adoauth2client_adapp FOREIGN KEY (ad_app_id) REFERENCES cadre.ad_app(ad_app_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_oauth2_client_token adoauth2client_adapp; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth2_client_token
    ADD CONSTRAINT adoauth2client_adapp FOREIGN KEY (ad_app_id) REFERENCES cadre.ad_app(ad_app_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_oauth2_client adoauth2client_aduser; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth2_client
    ADD CONSTRAINT adoauth2client_aduser FOREIGN KEY (ad_user_id) REFERENCES cadre.ad_user(ad_user_id);


--
-- Name: ad_oauth_client_roles adoauthclientroles_adoauthclient; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth_client_roles
    ADD CONSTRAINT adoauthclientroles_adoauthclient FOREIGN KEY (ad_oauth2_client_id) REFERENCES cadre.ad_oauth2_client(ad_oauth2_client_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_oauth_client_roles adoauthclientroles_adrole; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth_client_roles
    ADD CONSTRAINT adoauthclientroles_adrole FOREIGN KEY (ad_role_id) REFERENCES cadre.ad_role(ad_role_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_object_access adobjectaccess_adresourcetype; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_object_access
    ADD CONSTRAINT adobjectaccess_adresourcetype FOREIGN KEY (ad_resource_type_id) REFERENCES cadre.ad_resource_type(ad_resource_type_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_object_access adobjectaccess_adrole; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_object_access
    ADD CONSTRAINT adobjectaccess_adrole FOREIGN KEY (ad_role_id) REFERENCES cadre.ad_role(ad_role_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_oauth2_client_token adouath2clientoken_adoauth2client; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth2_client_token
    ADD CONSTRAINT adouath2clientoken_adoauth2client FOREIGN KEY (ad_oauth2_client_id) REFERENCES cadre.ad_oauth2_client(ad_oauth2_client_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_oauth2_client_token adouath2clientoken_aduser; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_oauth2_client_token
    ADD CONSTRAINT adouath2clientoken_aduser FOREIGN KEY (ad_user_id) REFERENCES cadre.ad_user(ad_user_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_process_para adprocesspara_adprocess; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_process_para
    ADD CONSTRAINT adprocesspara_adprocess FOREIGN KEY (ad_process_id) REFERENCES cadre.ad_process(ad_process_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_process_para adprocesspara_adreference; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_process_para
    ADD CONSTRAINT adprocesspara_adreference FOREIGN KEY (ad_reference_id) REFERENCES cadre.ad_reference(ad_reference_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_process_para adprocesspara_adreference1; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_process_para
    ADD CONSTRAINT adprocesspara_adreference1 FOREIGN KEY (ad_reference_value_id) REFERENCES cadre.ad_reference(ad_reference_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_reference adreference_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_reference
    ADD CONSTRAINT adreference_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_reference adreference_adtable; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_reference
    ADD CONSTRAINT adreference_adtable FOREIGN KEY (ad_table_id) REFERENCES cadre.ad_table(ad_table_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_ref_list adreflist_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_ref_list
    ADD CONSTRAINT adreflist_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_ref_list adreflist_adreference; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_ref_list
    ADD CONSTRAINT adreflist_adreference FOREIGN KEY (ad_reference_id) REFERENCES cadre.ad_reference(ad_reference_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_resource_type adresourcetype_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_resource_type
    ADD CONSTRAINT adresourcetype_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_tab adtab_adcolumn; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_tab
    ADD CONSTRAINT adtab_adcolumn FOREIGN KEY (parent_column_id) REFERENCES cadre.ad_column(ad_column_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_tab adtab_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_tab
    ADD CONSTRAINT adtab_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_tab adtab_adtable; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_tab
    ADD CONSTRAINT adtab_adtable FOREIGN KEY (ad_table_id) REFERENCES cadre.ad_table(ad_table_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_tab adtab_adwindow; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_tab
    ADD CONSTRAINT adtab_adwindow FOREIGN KEY (ad_window_id) REFERENCES cadre.ad_window(ad_window_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_attachment adtable_adattachment; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_attachment
    ADD CONSTRAINT adtable_adattachment FOREIGN KEY (ad_table_id) REFERENCES cadre.ad_table(ad_table_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_table adtable_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_table
    ADD CONSTRAINT adtable_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_treenode adtreenode_adwindow; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_treenode
    ADD CONSTRAINT adtreenode_adwindow FOREIGN KEY (ad_window_id) REFERENCES cadre.ad_window(ad_window_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_treenode adtreenode_parent; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_treenode
    ADD CONSTRAINT adtreenode_parent FOREIGN KEY (ad_treenode_parent_id) REFERENCES cadre.ad_treenode(ad_treenode_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_user_app aduserapp_adapp; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user_app
    ADD CONSTRAINT aduserapp_adapp FOREIGN KEY (ad_app_id) REFERENCES cadre.ad_app(ad_app_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_user_app aduserapp_aduser; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user_app
    ADD CONSTRAINT aduserapp_aduser FOREIGN KEY (ad_user_id) REFERENCES cadre.ad_user(ad_user_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_user_roles aduserroles_adrole; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user_roles
    ADD CONSTRAINT aduserroles_adrole FOREIGN KEY (ad_role_id) REFERENCES cadre.ad_role(ad_role_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_user_roles aduserroles_aduser; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_user_roles
    ADD CONSTRAINT aduserroles_aduser FOREIGN KEY (ad_user_id) REFERENCES cadre.ad_user(ad_user_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_variable advariable_adreference; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_variable
    ADD CONSTRAINT advariable_adreference FOREIGN KEY (ad_reference_id) REFERENCES cadre.ad_reference(ad_reference_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_window adwindow_adextension; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_window
    ADD CONSTRAINT adwindow_adextension FOREIGN KEY (ad_extension_id) REFERENCES cadre.ad_extension(ad_extension_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_window_access adwindowaccess_adrole; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_window_access
    ADD CONSTRAINT adwindowaccess_adrole FOREIGN KEY (ad_role_id) REFERENCES cadre.ad_role(ad_role_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ad_window_access adwindowaccess_adwindow; Type: FK CONSTRAINT; Schema: cadre; Owner: cadre
--

ALTER TABLE ONLY cadre.ad_window_access
    ADD CONSTRAINT adwindowaccess_adwindow FOREIGN KEY (ad_window_id) REFERENCES cadre.ad_window(ad_window_id) DEFERRABLE INITIALLY DEFERRED;


--
-- PostgreSQL database dump complete
--

