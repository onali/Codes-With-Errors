package com.example;
import java.util.StringTokenizer;
public final class JavaConventions {

    private static final char DOT = '.';

    private static final String PACKAGE_INFO = new String(TypeConstants.PACKAGE_INFO_NAME);

    private static final Scanner SCANNER = new Scanner(false, /*comment*/
            true, /*whitespace*/
            false, /*nls*/
            ClassFileConstants.JDK1_3, /*sourceLevel*/
            null, /*taskTag*/
            null, /*taskPriorities*/
            true);

    private  JavaConventions() {

    }

    public static boolean isOverlappingRoots(IPath rootPath1, IPath rootPath2) {
        if (rootPath1 == null || rootPath2 == null) {
            return false;
        }
        return rootPath1.isPrefixOf(rootPath2) || rootPath2.isPrefixOf(rootPath1);
    }

    private static synchronized char[] scannedIdentifier(String id, String sourceLevel, String complianceLevel) {
        if (id == null) {
            return null;
        }
        // Set scanner for given source and compliance levels
        SCANNER.sourceLevel = sourceLevel == null ? ClassFileConstants.JDK1_3 : CompilerOptions.versionToJdkLevel(sourceLevel);
        SCANNER.complianceLevel = complianceLevel == null ? ClassFileConstants.JDK1_3 : CompilerOptions.versionToJdkLevel(complianceLevel);
        try {
            SCANNER.setSource(id.toCharArray());
            int token = SCANNER.scanIdentifier();
            if (token != TerminalTokens.TokenNameIdentifier)
                return null;
            if (// to handle case where we had an ArrayIndexOutOfBoundsException
                    SCANNER.currentPosition == SCANNER.eofPosition) {
                try {
                    return SCANNER.getCurrentIdentifierSource();
                } catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }
            } else {
                return null;
            }
        } catch (InvalidInputException e) {
            return null;
        }
    }

    public static IStatus validateCompilationUnitName(String name) {
        return validateCompilationUnitName(name, CompilerOptions.VERSION_1_3, CompilerOptions.VERSION_1_3);
    }

    public static IStatus validateCompilationUnitName(String name, String sourceLevel, String complianceLevel) {
        if (name == null) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_unit_nullName, null);
        }
        if (!org.eclipse.jdt.internal.core.util.Util.isJavaLikeFileName(name)) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_unit_notJavaName, null);
        }
        String identifier;
        int index;
        index = name.lastIndexOf('.');
        if (index == -1) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_unit_notJavaName, null);
        }
        identifier = name.substring(0, index);
        // the package-level spec (replaces package.html)
        if (!identifier.equals(PACKAGE_INFO)) {
            IStatus status = validateIdentifier(identifier, sourceLevel, complianceLevel);
            if (!status.isOK()) {
                return status;
            }
        }
        IStatus status = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
        if (!status.isOK()) {
            return status;
        }
        return JavaModelStatus.VERIFIED_OK;
    }

    public static IStatus validateClassFileName(String name) {
        return validateClassFileName(name, CompilerOptions.VERSION_1_3, CompilerOptions.VERSION_1_3);
    }

    public static IStatus validateClassFileName(String name, String sourceLevel, String complianceLevel) {
        if (name == null) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_classFile_nullName, null);
        }
        if (!org.eclipse.jdt.internal.compiler.util.Util.isClassFileName(name)) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_classFile_notClassFileName, null);
        }
        String identifier;
        int index;
        index = name.lastIndexOf('.');
        if (index == -1) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_classFile_notClassFileName, null);
        }
        identifier = name.substring(0, index);
        // the package-level spec (replaces package.html)
        if (!identifier.equals(PACKAGE_INFO)) {
            IStatus status = validateIdentifier(identifier, sourceLevel, complianceLevel);
            if (!status.isOK()) {
                return status;
            }
        }
        IStatus status = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
        if (!status.isOK()) {
            return status;
        }
        return JavaModelStatus.VERIFIED_OK;
    }

    public static IStatus validateFieldName(String name) {
        return validateIdentifier(name, CompilerOptions.VERSION_1_3, CompilerOptions.VERSION_1_3);
    }

    public static IStatus validateFieldName(String name, String sourceLevel, String complianceLevel) {
        return validateIdentifier(name, sourceLevel, complianceLevel);
    }

    public static IStatus validateIdentifier(String id) {
        return validateIdentifier(id, CompilerOptions.VERSION_1_3, CompilerOptions.VERSION_1_3);
    }

    public static IStatus validateIdentifier(String id, String sourceLevel, String complianceLevel) {
        if (scannedIdentifier(id, sourceLevel, complianceLevel) != null) {
            return JavaModelStatus.VERIFIED_OK;
        } else {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.bind(Messages.convention_illegalIdentifier, id), null);
        }
    }

    public static IStatus validateImportDeclaration(String name) {
        return validateImportDeclaration(name, CompilerOptions.VERSION_1_3, CompilerOptions.VERSION_1_3);
    }

    public static IStatus validateImportDeclaration(String name, String sourceLevel, String complianceLevel) {
        if (name == null || name.length() == 0) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_import_nullImport, null);
        }
        if (name.charAt(name.length() - 1) == '*') {
            if (name.charAt(name.length() - 2) == '.') {
                return validatePackageName(name.substring(0, name.length() - 2), sourceLevel, complianceLevel);
            } else {
                return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_import_unqualifiedImport, null);
            }
        }
        return validatePackageName(name, sourceLevel, complianceLevel);
    }

    public static IStatus validateJavaTypeName(String name) {
        return validateJavaTypeName(name, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
    }

    public static IStatus validateJavaTypeName(String name, String sourceLevel, String complianceLevel) {
        if (name == null) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_type_nullName, null);
        }
        String trimmed = name.trim();
        if (!name.equals(trimmed)) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_type_nameWithBlanks, null);
        }
        int index = name.lastIndexOf('.');
        char[] scannedID;
        if (index == -1) {
            // simple name
            scannedID = scannedIdentifier(name, sourceLevel, complianceLevel);
        } else {
            // qualified name
            String pkg = name.substring(0, index).trim();
            IStatus status = validatePackageName(pkg, sourceLevel, complianceLevel);
            if (!status.isOK()) {
                return status;
            }
            String type = name.substring(index + 1).trim();
            scannedID = scannedIdentifier(type, sourceLevel, complianceLevel);
        }
        if (scannedID != null) {
            IStatus status = ResourcesPlugin.getWorkspace().validateName(new String(scannedID), IResource.FILE);
            if (!status.isOK()) {
                return status;
            }
            if (CharOperation.contains('$', scannedID)) {
                return new Status(IStatus.WARNING, JavaCore.PLUGIN_ID, -1, Messages.convention_type_dollarName, null);
            }
            if ((scannedID.length > 0 && ScannerHelper.isLowerCase(scannedID[0]))) {
                return new Status(IStatus.WARNING, JavaCore.PLUGIN_ID, -1, Messages.convention_type_lowercaseName, null);
            }
            return JavaModelStatus.VERIFIED_OK;
        } else {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.bind(Messages.convention_type_invalidName, name), null);
        }
    }

    public static IStatus validateMethodName(String name) {
        return validateMethodName(name, CompilerOptions.VERSION_1_3, CompilerOptions.VERSION_1_3);
    }

    public static IStatus validateMethodName(String name, String sourceLevel, String complianceLevel) {
        return validateIdentifier(name, sourceLevel, complianceLevel);
    }

    public static IStatus validatePackageName(String name) {
        return validatePackageName(name, CompilerOptions.VERSION_1_3, CompilerOptions.VERSION_1_3);
    }

    public static IStatus validatePackageName(String name, String sourceLevel, String complianceLevel) {
        if (name == null) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_package_nullName, null);
        }
        int length;
        if ((length = name.length()) == 0) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_package_emptyName, null);
        }
        if (name.charAt(0) == DOT || name.charAt(length - 1) == DOT) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_package_dotName, null);
        }
        if (CharOperation.isWhitespace(name.charAt(0)) || CharOperation.isWhitespace(name.charAt(name.length() - 1))) {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_package_nameWithBlanks, null);
        }
        int dot = 0;
        while (dot != -1 && dot < length - 1) {
            if ((dot = name.indexOf(DOT, dot + 1)) != -1 && dot < length - 1 && name.charAt(dot + 1) == DOT) {
                return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.convention_package_consecutiveDotsName, null);
            }
        }
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        //$NON-NLS-1$
        StringTokenizer st = new StringTokenizer(name, ".");
        boolean firstToken = true;
        IStatus warningStatus = null;
        while (st.hasMoreTokens()) {
            String typeName = st.nextToken();
            // grammar allows spaces
            typeName = typeName.trim();
            char[] scannedID = scannedIdentifier(typeName, sourceLevel, complianceLevel);
            if (scannedID == null) {
                return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.bind(Messages.convention_illegalIdentifier, typeName), null);
            }
            IStatus status = workspace.validateName(new String(scannedID), IResource.FOLDER);
            if (!status.isOK()) {
                return status;
            }
            if (firstToken && scannedID.length > 0 && ScannerHelper.isUpperCase(scannedID[0])) {
                if (warningStatus == null) {
                    warningStatus = new Status(IStatus.WARNING, JavaCore.PLUGIN_ID, -1, Messages.convention_package_uppercaseName, null);
                }
            }
            firstToken = false;
        }
        if (warningStatus != null) {
            return warningStatus;
        }
        return JavaModelStatus.VERIFIED_OK;
    }

    public static IJavaModelStatus validateClasspath(IJavaProject javaProject, IClasspathEntry[] rawClasspath, IPath projectOutputLocation) {
        return ClasspathEntry.validateClasspath(javaProject, rawClasspath, projectOutputLocation);
    }
    
    public static IJavaModelStatus validateClasspathEntry(IJavaProject project, IClasspathEntry entry, boolean checkSourceAttachment) {
        return ClasspathEntry.validateClasspathEntry(project, entry, checkSourceAttachment, /*not referred by container*/
                false);
    }
    
    public static IStatus validateTypeVariableName(String name) {
        return validateIdentifier(name, CompilerOptions.VERSION_1_3, CompilerOptions.VERSION_1_3);
    }

   
    public static IStatus validateTypeVariableName(String name, String sourceLevel, String complianceLevel) {
        return validateIdentifier(name, sourceLevel, complianceLevel);
    }
}