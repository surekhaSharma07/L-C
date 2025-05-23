//No, the provided code does not follow SRP. The Book class violates the principle because it has multiple responsibilities:
//
//Managing the book's data (e.g., title, author, current page).
//Each responsibility should be split into separate classes to ensure the Book class follows to SRP.

class Book {
    private $title;
    private $author;
    private $currentPage;

    public function __construct(string $title, string $author) {
        $this->title = $title;
        $this->author = $author;
        $this->currentPage = 1;
    }

    public function getTitle(): string {
        return $this->title;
    }

    public function getAuthor(): string {
        return $this->author;
    }

    public function turnToNextPage(): void {
        $this->currentPage++;
    }

    public function getCurrentPageContent(): string {
        return "Content of page " . $this->currentPage;
    }
}

class BookStorage {
    public function save(Book $book): void {
        $filePath = '/documents/' . $book->getTitle() . ' - ' . $book->getAuthor();
        file_put_contents($filePath, serialize($book));
    }
}

interface Printer {
    public function printContent(string $content): void;
}

class PlainTextPrinter implements Printer {
    public function printContent(string $content): void {
        echo $content;
    }
}

class HtmlPrinter implements Printer {
    public function printContent(string $content): void {
        echo '<div class="page-content">' . $content . '</div>';
    }
}